package ru.klimachkov.workplaces.services;

import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.workplaces.dsl.predicates.ReservationPredicateBuilder;
import ru.klimachkov.workplaces.dto.reservation.ReservationCreationDto;
import ru.klimachkov.workplaces.dto.reservation.ReservationFilterModel;
import ru.klimachkov.workplaces.dto.reservation.ReservationResponseDto;
import ru.klimachkov.workplaces.dto.reservation.ReservationUpdateDto;
import ru.klimachkov.workplaces.exceptions.EntityConflictException;
import ru.klimachkov.workplaces.exceptions.InvalidReservationConfigurationException;
import ru.klimachkov.workplaces.exceptions.NotFoundEntityException;
import ru.klimachkov.workplaces.exceptions.ReservationAccessDeniedException;
import ru.klimachkov.workplaces.mappers.ReservationMapper;
import ru.klimachkov.workplaces.models.Employee;
import ru.klimachkov.workplaces.models.Reservation;
import ru.klimachkov.workplaces.models.User;
import ru.klimachkov.workplaces.models.Workplace;
import ru.klimachkov.workplaces.models.WorkplaceType;
import ru.klimachkov.workplaces.repositories.ReservationRepository;
import ru.klimachkov.workplaces.util.UserUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Реализация сервиса {@link ReservationService} для работы с данными о бронированиях рабочих мест.
 *
 * @see ReservationRepository
 * @see ReservationMapper
 * @see ReservationPredicateBuilder
 * @see WorkplaceService
 * @see EmployeeService
 * @see UserService
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    /**
     * Репозиторий для работы с бронированиями.
     */
    private final ReservationRepository reservationRepository;

    /**
     * Маппер для преобразования {@link Reservation} в DTO представление.
     */
    private final ReservationMapper reservationMapper;

    /**
     * Билдер предиката для формирования условий фильтрации бронирований.
     */
    private final ReservationPredicateBuilder reservationPredicateBuilder;

    /**
     * Сервис для работы с рабочими местами.
     */
    private final WorkplaceService workplaceService;

    /**
     * Сервис для работы с сотрудниками.
     */
    private final EmployeeService employeeService;

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    @Value("${app.datetime-format}")
    private String dateTimeFormat;

    /**
     * Возвращает бронирование по его идентификатору.
     *
     * <p>При получении бронирования загружаются связанные сущности,
     * необходимые для формирования полного DTO представления.</p>
     *
     * @param id идентификатор бронирования
     * @return DTO представление бронирования
     *
     * @throws NotFoundEntityException если бронирование не найдено
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDto findById(Long id) {
        Reservation reservation = reservationRepository.findWithRelationsById(id)
                .orElseThrow(() -> createNotFoundReservationException(id));

        return reservationMapper.toResponseDto(reservation);
    }

    /**
     * Выполняет поиск бронирований с учетом фильтрации и пагинации.
     *
     * <p>Фильтрация выполняется с использованием {@link ReservationPredicateBuilder}.</p>
     *
     * @param reservationFilterModel модель с параметрами фильтрации бронирований
     * @param pageable параметры пагинации
     *
     * @return страница DTO представлений бронирований
     */
    @Override
    @Transactional(readOnly = true)
    public PagedModel<ReservationResponseDto> findAll(ReservationFilterModel reservationFilterModel, Pageable pageable) {
        Predicate predicate = reservationPredicateBuilder.build(reservationFilterModel);
        Page<ReservationResponseDto> reservationsPage = reservationRepository.findAll(predicate, pageable)
                .map(reservationMapper::toResponseDto);

        return new PagedModel<>(reservationsPage);
    }

    /**
     * Создает новое бронирование рабочего места.
     *
     * <p>Перед созданием выполняется проверка прав пользователя на возможность создания,
     * корректности временного интервала и отсутствия пересечений
     * с существующими бронированиями рабочего места и сотрудника.</p>
     *
     * @param reservationCreationDto данные для создания бронирования
     *
     * @return DTO представление созданного бронирования
     *
     * @throws ReservationAccessDeniedException если пользователь не имеет права
     * создать бронирование
     * @throws InvalidReservationConfigurationException если временной интервал
     * бронирования некорректен
     * @throws EntityConflictException если существует пересечение с другим бронированием
     */
    @Transactional
    public ReservationResponseDto create(ReservationCreationDto reservationCreationDto) {
        Workplace workplace = workplaceService.findById(reservationCreationDto.workplaceId());

        checkAccessToReserveWorkplace(workplace, reservationCreationDto.employeeId(), null);

        LocalDateTime startTime = truncateTime(reservationCreationDto.startTime());
        LocalDateTime endTime = truncateTime(reservationCreationDto.endTime());

        validateTime(startTime, endTime);

        validateIntersections(startTime, endTime, reservationCreationDto.workplaceId(),
                reservationCreationDto.employeeId(), null);

        Employee employee = employeeService.findById(reservationCreationDto.employeeId());

        Reservation reservation = Reservation.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();

        workplace.addReservation(reservation);
        employee.addReservation(reservation);

        return reservationMapper.toResponseDto(reservationRepository.save(reservation));
    }

    /**
     * Обновляет существующее бронирование.
     *
     * <p>Выполняет проверку прав доступа, корректности временного интервала
     * и отсутствия конфликтов с другими бронированиями.</p>
     *
     * @param id идентификатор бронирования
     * @param reservationUpdateDto новые данные бронирования
     *
     * @return DTO представление обновленного бронирования
     *
     * @throws NotFoundEntityException если бронирование не найдено
     * @throws ReservationAccessDeniedException если пользователь не имеет права
     * изменять бронирование
     * @throws InvalidReservationConfigurationException если временной интервал
     * бронирования некорректен
     * @throws EntityConflictException если существует пересечение с другим бронированием
     */
    @Transactional
    public ReservationResponseDto update(Long id, ReservationUpdateDto reservationUpdateDto) {
        Reservation reservation = findByIdWithoutEagerRelations(id);

        checkAccessToReserveWorkplace(reservation.getWorkplace(),
                reservationUpdateDto.employeeId(), reservation.getEmployee().getId());

        LocalDateTime startTime = truncateTime(reservationUpdateDto.startTime());
        LocalDateTime endTime = truncateTime(reservationUpdateDto.endTime());

        validateTime(startTime, endTime);

        validateIntersections(
                startTime, endTime,
                reservation.getWorkplace().getId(),
                reservationUpdateDto.employeeId(),
                reservation.getId());

        reservation.setEmployee(employeeService.findById(reservationUpdateDto.employeeId()));
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);

        reservationRepository.flush();
        return reservationMapper.toResponseDto(reservation);
    }

    /**
     * Удаляет бронирование по идентификатору.
     *
     * <p>Пользователь с административными правами может удалить любое бронирование.
     * Обычный пользователь может удалить только собственное бронирование
     * или бронирование, связанное с закрепленным за ним рабочим местом.</p>
     *
     * @param id идентификатор бронирования
     *
     * @throws ReservationAccessDeniedException если пользователь не имеет права
     * удалить бронирование
     */
    @Override
    @Transactional
    public void delete(Long id) {
        User currentUser = userService.getMe();

        if (!UserUtils.isAdmin(currentUser)) {
            Reservation reservation = reservationRepository.findById(id).orElse(null);
            if (reservation == null) {
                return;
            }

            if (!canEmployeeDeleteReservation(currentUser, reservation)) {
                throw new ReservationAccessDeniedException(
                        "There is no permission to delete this reservation",
                        "error.reservation.no_permission_to_delete"
                );
            }
        }

        reservationRepository.deleteById(id);
    }

    /**
     * Удаляет все бронирования, связанные с указанным рабочим местом.
     *
     * @param workplaceId идентификатор рабочего места
     */
    @Override
    @Transactional
    public void deleteByWorkplace(Long workplaceId) {
        reservationRepository.deleteByWorkplaceId(workplaceId);
    }

    /**
     * Удаляет все бронирования, связанные с указанным сотрудником.
     *
     * @param employeeId идентификатор сотрудника
     */
    @Override
    @Transactional
    public void deleteByEmployee(Long employeeId) {
        reservationRepository.deleteByEmployeeId(employeeId);
    }

    /**
     * Возвращает бронирование по идентификатору без загрузки связанных сущностей.
     *
     * @param id идентификатор бронирования
     * @return найденное бронирование
     *
     * @throws NotFoundEntityException если бронирование не найдено
     */
    @NonNull
    private Reservation findByIdWithoutEagerRelations(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> createNotFoundReservationException(id));
    }

    /**
     * Проверяет наличие прав пользователя на создание или изменение бронирования
     * указанного рабочего места.
     *
     * <p>Администраторы имеют доступ ко всем рабочим местам.</p>
     * <p>Для индивидуального рабочего места доступ предоставляется только сотруднику,
     * за которым оно закреплено. Он может зарезервировать рабочее место на себя или другого сотрудника.</p>
     * <p>Hot-desk сотрудник может бронировать только на себя</p>
     *
     * @param workplace рабочее место, для которого выполняется проверка доступа
     * @param targetEmployeeId идентификатор сотрудника, для которого создается
     * или изменяется бронирование
     * @param reservationEmployeeId идентификатор сотрудника текущего бронирования
     * (может быть {@code null})
     *
     * @throws ReservationAccessDeniedException если пользователь не имеет права
     * выполнять бронирование
     */
    private void checkAccessToReserveWorkplace(Workplace workplace, Long targetEmployeeId, Long reservationEmployeeId) {
        User currentUser = userService.getMe();
        if (UserUtils.isAdmin(currentUser)) {
            return;
        }

        Long userEmployeeId = employeeService.getIdByUserId(currentUser.getId());
        if (workplace.getType() == WorkplaceType.INDIVIDUAL) {
            if (!userEmployeeId.equals(workplace.getEmployee().getId())) {
                throw new ReservationAccessDeniedException(
                        "Only an administrator or the assigned employee can reserve this individual workspace",
                       "error.workplace.reservation_individual_not_allowed"
                );
            }

        } else if (!userEmployeeId.equals(targetEmployeeId)
                || (reservationEmployeeId != null && !reservationEmployeeId.equals(targetEmployeeId))
        ) {
            throw new ReservationAccessDeniedException(
                    "Only an administrator or the employee themselves can reserve a hot desk",
                    "error.workplace.reservation_hot_desk_not_allowed"
            );
        }
    }

    /**
     * Убирает из времени бронирования секунды и наносекунды.
     *
     * @param time исходное время
     * @return время с точностью до минут
     */
    private LocalDateTime truncateTime(LocalDateTime time) {
        return time.truncatedTo(ChronoUnit.MINUTES);
    }

    /**
     * Проверяет корректность временного интервала бронирования.
     *
     * <p>Время начала бронирования должно быть раньше времени окончания.</p>
     *
     * @param startTime время начала бронирования
     * @param endTime время окончания бронирования
     *
     * @throws InvalidReservationConfigurationException если время начала
     * не предшествует времени окончания
     */
    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new InvalidReservationConfigurationException(
                    "The start time of the reservation must be before the end time",
                    "error.reservation.start_time_before_end_time"
            );
        }
    }

    /**
     * Проверяет отсутствие пересечений бронирования с существующими бронированиями.
     *
     * <p>Проверяется наличие конфликтов как для рабочего места,
     * так и для сотрудника.</p>
     *
     * @param startTime время начала бронирования
     * @param endTime время окончания бронирования
     * @param workplaceId идентификатор рабочего места
     * @param employeeId идентификатор сотрудника
     * @param excludeReservationId идентификатор бронирования, которое необходимо
     * исключить из проверки (может быть {@code null})
     *
     * @throws EntityConflictException если найдено пересекающееся бронирование
     */
    private void validateIntersections(LocalDateTime startTime,
                                       LocalDateTime endTime,
                                       Long workplaceId,
                                       Long employeeId,
                                       Long excludeReservationId
    ) {
        checkExistsReservationIntersectionByWorkplace(startTime, endTime, workplaceId, excludeReservationId);
        checkExistsReservationIntersectionByEmployee(startTime, endTime, employeeId, excludeReservationId);
    }

    /**
     * Проверяет наличие пересечения бронирования с другими бронированиями
     * указанного рабочего места.
     *
     * @param startTime время начала бронирования
     * @param endTime время окончания бронирования
     * @param workplaceId идентификатор рабочего места
     * @param reservationId идентификатор текущего бронирования,
     * которое необходимо исключить из проверки (может быть {@code null})
     *
     * @throws EntityConflictException если рабочее место уже занято
     */
    private void checkExistsReservationIntersectionByWorkplace(
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long workplaceId,
            Long reservationId
    ) {
        Supplier<Optional<Reservation>> intersectionFinder;

        if (reservationId == null) {
            intersectionFinder = () -> reservationRepository
                    .findFirstByWorkplaceIdAndEndTimeGreaterThanAndStartTimeLessThan(workplaceId, startTime, endTime);
        } else {
            intersectionFinder = () -> reservationRepository
                    .findFirstByWorkplaceIdAndEndTimeGreaterThanAndStartTimeLessThanAndIdNot(
                            workplaceId, startTime, endTime, reservationId
                    );
        }

        checkExistsReservationIntersection(
                intersectionFinder,
                "The workplace has already been reserved from %s to %s",
                "error.workplace.already_reserved"
        );
    }

    /**
     * Проверяет наличие пересечения бронирования с другими бронированиями
     * указанного сотрудника.
     *
     * @param startTime время начала бронирования
     * @param endTime время окончания бронирования
     * @param employeeId идентификатор сотрудника
     * @param reservationId идентификатор текущего бронирования,
     * которое необходимо исключить из проверки (может быть {@code null})
     *
     * @throws EntityConflictException если у сотрудника уже есть бронирование
     * в указанный временной интервал
     */
    private void checkExistsReservationIntersectionByEmployee(
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long employeeId,
            Long reservationId
    ) {
        Supplier<Optional<Reservation>> intersectionFinder;

        if (reservationId == null) {
            intersectionFinder = () -> reservationRepository
                    .findFirstByEmployeeIdAndEndTimeGreaterThanAndStartTimeLessThan(employeeId, startTime, endTime);
        } else {
            intersectionFinder = () -> reservationRepository
                    .findFirstByEmployeeIdAndEndTimeGreaterThanAndStartTimeLessThanAndIdNot(
                            employeeId, startTime, endTime, reservationId
                    );
        }

        checkExistsReservationIntersection(
                intersectionFinder,
                "The employee already has a reservation from %s to %s",
                "error.employee.already_reserved"
        );
    }

    /**
     * Проверяет результат поиска пересечения бронирований.
     *
     * <p>Если найдено существующее бронирование, выбрасывается исключение
     * с информацией о конфликтующем временном интервале.</p>
     *
     * @param intersectionFinder функция поиска пересекающегося бронирования
     * @param errorMessage сообщение ошибки с параметрами временного интервала
     * @param errorCode код локализованного сообщения ошибки
     *
     * @throws EntityConflictException если существует пересекающееся бронирование
     */
    private void checkExistsReservationIntersection(
            Supplier<Optional<Reservation>> intersectionFinder,
            String errorMessage,
            String errorCode
    ) {
        intersectionFinder.get().ifPresent(reservation -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

            String startTimeFormatted = reservation.getStartTime().format(formatter);
            String endTimeFormatted = reservation.getEndTime().format(formatter);

            throw new EntityConflictException(
                    errorMessage.formatted(startTimeFormatted, endTimeFormatted),
                    errorCode,
                    new Object[]{startTimeFormatted, endTimeFormatted}
            );
        });
    }

    /**
     * Проверяет возможность удаления бронирования сотрудником.
     *
     * <p>Сотрудник может удалить собственное бронирование или бронирование,
     * созданное для закрепленного за ним рабочего места.</p>
     *
     * @param user пользователь, выполняющий операцию
     * @param reservation бронирование для проверки доступа
     *
     * @return {@code true}, если сотрудник имеет право удалить бронирование,
     * иначе {@code false}
     */
    private boolean canEmployeeDeleteReservation(User user, Reservation reservation) {
        Long userEmployeeId = employeeService.getIdByUserId(user.getId());
        boolean isReservationOwner = userEmployeeId.equals(reservation.getEmployee().getId());

        return isReservationOwner ||
                (reservation.getWorkplace().getEmployee() != null
                        && reservation.getWorkplace().getEmployee().getId().equals(userEmployeeId));
    }

    /**
     * Создает исключение о ненайденном бронировании.
     *
     * @param id идентификатор бронирования
     *
     * @return исключение с информацией о ненайденном бронировании
     */
    private NotFoundEntityException createNotFoundReservationException(Long id) {
        return new NotFoundEntityException(
                "Reservation with ID %d not found".formatted(id),
                "error.reservation.not_found",
                new Object[]{id}
        );
    }
}
