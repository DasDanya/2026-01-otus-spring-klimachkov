package ru.klimachkov.workplaces.services;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.workplaces.dsl.predicates.WorkplacePredicateBuilder;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceCreationDto;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceFilterModel;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceResponseDto;
import ru.klimachkov.workplaces.exceptions.InvalidWorkplaceConfigurationException;
import ru.klimachkov.workplaces.exceptions.EntityConflictException;
import ru.klimachkov.workplaces.exceptions.NotFoundEntityException;
import ru.klimachkov.workplaces.mappers.WorkplaceMapper;
import ru.klimachkov.workplaces.models.Employee;
import ru.klimachkov.workplaces.models.Location;
import ru.klimachkov.workplaces.models.Workplace;
import ru.klimachkov.workplaces.models.WorkplaceType;
import ru.klimachkov.workplaces.repositories.WorkplaceRepository;

/**
 * Реализация сервиса {@link WorkplaceService} для работы с данными о рабочих местах.
 *
 * @see WorkplaceRepository
 * @see WorkplaceMapper
 * @see WorkplacePredicateBuilder
 * @see LocationService
 * @see EmployeeService
 */
@Service
@RequiredArgsConstructor
public class WorkplaceServiceImpl implements WorkplaceService {

    /**
     * Максимальная вместимость рабочего места.
     */
    private static final int WORKPLACE_CAPACITY = 1;

    /**
     * Репозиторий для работы с рабочими местами.
     */
    private final WorkplaceRepository workplaceRepository;

    /**
     * Маппер для преобразования сущностей {@link Workplace} в DTO представление.
     */
    private final WorkplaceMapper workplaceMapper;

    /**
     * Билдер предиката для формирования условий фильтрации рабочих мест.
     */
    private final WorkplacePredicateBuilder workplacePredicateBuilder;

    /**
     * Сервис для работы с локациями.
     */
    private final LocationService locationService;

    /**
     * Сервис для работы с сотрудниками.
     */
    private final EmployeeService employeeService;

    /**
     * Возвращает рабочее место по его идентификатору.
     *
     * @param id идентификатор рабочего места
     * @return найденное рабочее место
     *
     * @throws NotFoundEntityException если рабочее место не найдено
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public Workplace findById(Long id) {
        return workplaceRepository.findById(id)
                .orElseThrow(() -> createWorkplaceNotFoundException(id));
    }

    /**
     * Возвращает DTO представление рабочего места с загруженными связями.
     *
     * @param id идентификатор рабочего места
     * @return DTO представление рабочего места
     *
     * @throws NotFoundEntityException если рабочее место не найдено
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public WorkplaceResponseDto findByIdWithRelations(Long id) {
        Workplace workplace = workplaceRepository.findWithRelationsById(id)
                .orElseThrow(() -> createWorkplaceNotFoundException(id));

        return workplaceMapper.toResponseDto(workplace);
    }

    /**
     * Выполняет поиск рабочих мест с учетом фильтрации и пагинации.
     *
     * <p>Фильтрация выполняется с использованием {@link WorkplacePredicateBuilder}.</p>
     *
     * @param workplaceFilterModel модель с параметрами фильтрации рабочих мест
     * @param pageable параметры пагинации
     *
     * @return страница DTO представлений рабочих мест
     */
    @Override
    @Transactional(readOnly = true)
    public PagedModel<WorkplaceResponseDto> findAll(WorkplaceFilterModel workplaceFilterModel, Pageable pageable) {
        Predicate predicate = workplacePredicateBuilder.build(workplaceFilterModel);
        Page<WorkplaceResponseDto> workplacesPage = workplaceRepository.findAll(predicate, pageable)
                .map(workplaceMapper::toResponseDto);

        return new PagedModel<>(workplacesPage);
    }

    /**
     * Создает новое рабочее место.
     *
     * <p>Проверяет корректность конфигурации рабочего места,
     * существование локации и сотрудника, а также уникальность
     * имени рабочего места в пределах локации.</p>
     *
     * <p>Для рабочего места типа {@link WorkplaceType#INDIVIDUAL}
     * необходимо указать сотрудника, за которым оно будет закреплено.
     * Также проверяется, что сотрудник не имеет другого индивидуального
     * рабочего места.</p>
     *
     * <p>Для рабочего места типа {@link WorkplaceType#HOT_DESK}
     * сотрудник не должен быть указан, так как такое рабочее место
     * является общим и не имеет постоянного владельца.</p>
     *
     * @param workplaceCreationDto данные для создания рабочего места
     *
     * @return DTO представление созданного рабочего места
     *
     * @throws NotFoundEntityException если локация или сотрудник не найдены
     * @throws EntityConflictException если рабочее место с таким именем
     * уже существует в указанной локации
     * @throws InvalidWorkplaceConfigurationException если конфигурация рабочего
     * места не соответствует выбранному типу
     */
    @Override
    @Transactional
    public WorkplaceResponseDto create(WorkplaceCreationDto workplaceCreationDto) {
        Location location = locationService.findById(workplaceCreationDto.locationId());

        checkExistsWorkplaceNameInLocation(workplaceCreationDto.name(), workplaceCreationDto.locationId(), null);

        Employee employee = workplaceCreationDto.employeeId() == null
                ? null
                : employeeService.findById(workplaceCreationDto.employeeId());

        checkWorkplaceConfiguration(workplaceCreationDto);

        Workplace workplace = Workplace.builder()
                .name(workplaceCreationDto.name())
                .type(workplaceCreationDto.type())
                .capacity(WORKPLACE_CAPACITY)
                .employee(employee)
                .build();

        location.addWorkplace(workplace);

        return workplaceMapper.toResponseDto(workplaceRepository.save(workplace));
    }

    /**
     * Изменяет название рабочего места.
     *
     * <p>Проверяет уникальность нового названия в пределах локации.</p>
     *
     * @param id идентификатор рабочего места
     * @param name новое название рабочего места
     *
     * @return DTO представление рабочего места с обновленным названием
     *
     * @throws NotFoundEntityException если рабочее место не найдено
     * @throws EntityConflictException если рабочее место с таким названием
     * уже существует в локации
     */
    @Override
    @Transactional
    public WorkplaceResponseDto rename(Long id, String name) {
        Workplace workplace = findById(id);

        checkExistsWorkplaceNameInLocation(name, workplace.getLocation().getId(), workplace.getId());

        workplace.setName(name);

        workplaceRepository.flush();

        return workplaceMapper.toResponseDto(workplace);
    }

    /**
     * Преобразует рабочее место в тип {@link WorkplaceType#HOT_DESK}.
     *
     * <p>При преобразовании рабочее место освобождается от закрепленного
     * сотрудника.</p>
     *
     * @param id идентификатор рабочего места
     *
     * @return DTO представление рабочего места
     *
     * @throws NotFoundEntityException если рабочее место не найдено
     */
    @Override
    @Transactional
    public WorkplaceResponseDto convertToHotDesk(Long id) {
        Workplace workplace = findById(id);
        if (workplace.getType() != WorkplaceType.HOT_DESK) {
            workplace.setType(WorkplaceType.HOT_DESK);
            workplace.setEmployee(null);

            workplaceRepository.flush();
        }

        return workplaceMapper.toResponseDto(workplace);
    }

    /**
     * Преобразует рабочее место в тип {@link WorkplaceType#INDIVIDUAL}.
     *
     * <p>При преобразовании рабочее место закрепляется за указанным сотрудником.</p>
     *
     * @param id идентификатор рабочего места
     * @param employeeId идентификатор сотрудника
     *
     * @return DTO представление рабочего места
     *
     * @throws NotFoundEntityException если рабочее место или сотрудник не найдены
     * @throws InvalidWorkplaceConfigurationException если сотрудник уже имеет
     * закрепленное индивидуальное рабочее место
     */
    @Override
    @Transactional
    public WorkplaceResponseDto convertToIndividual(Long id, Long employeeId) {
        Workplace workplace = findById(id);
        if (workplace.getType() != WorkplaceType.INDIVIDUAL) {
            checkExistsEmployeeWithIndividualWorkplace(employeeId, null);

            Employee employee = employeeService.findById(employeeId);

            workplace.setType(WorkplaceType.INDIVIDUAL);
            workplace.setEmployee(employee);

            workplaceRepository.flush();
        }

        return workplaceMapper.toResponseDto(workplace);
    }

    /**
     * Заменяет сотрудника, закрепленного за индивидуальным рабочим местом.
     *
     * @param id идентификатор рабочего места
     * @param employeeId идентификатор нового сотрудника
     *
     * @return DTO представление рабочего места
     *
     * @throws NotFoundEntityException если рабочее место или сотрудник не найдены
     * @throws InvalidWorkplaceConfigurationException если рабочее место
     * не является индивидуальным или сотрудник уже закреплен за другим индивидуальным
     * рабочим местом
     */
    @Override
    @Transactional
    public WorkplaceResponseDto replaceEmployee(Long id, Long employeeId) {
        Workplace workplace = findById(id);

        if (workplace.getType() != WorkplaceType.INDIVIDUAL) {
            throw new InvalidWorkplaceConfigurationException(
                    "It is not possible to replace an employee at a non-individual workplace",
                    "error.workplace.not_possible_to_replace_employee"
            );
        }

        if (!workplace.getEmployee().getId().equals(employeeId)) {
            checkExistsEmployeeWithIndividualWorkplace(employeeId, id);

            Employee employee = employeeService.findById(employeeId);
            workplace.setEmployee(employee);
            workplaceRepository.flush();
        }

        return workplaceMapper.toResponseDto(workplace);
    }

    /**
     * Удаляет рабочее место по его идентификатору.
     *
     * @param id идентификатор рабочего места
     */
    @Override
    @Transactional
    public void delete(Long id) {
        workplaceRepository.deleteById(id);
    }

    /**
     * Удаляет все рабочие места, принадлежащие указанной локации.
     *
     * @param locationId идентификатор локации
     */
    @Override
    @Transactional
    public void deleteByLocation(Long locationId) {
        workplaceRepository.deleteByLocationId(locationId);
    }

    /**
     * Удаляет рабочие места, закрепленные за указанным сотрудником.
     *
     * @param employeeId идентификатор сотрудника
     */
    @Override
    @Transactional
    public void deleteByEmployee(Long employeeId) {
        workplaceRepository.deleteByEmployeeId(employeeId);
    }

    /**
     * Проверяет наличие рабочего места с указанным названием в локации.
     *
     * <p>При передаче идентификатора рабочего места оно исключается
     * из проверки.</p>
     *
     * @param name название рабочего места
     * @param locationId идентификатор локации
     * @param workplaceId идентификатор рабочего места, которое необходимо исключить
     *
     * @throws EntityConflictException если рабочее место с таким названием
     * уже существует в локации
     */
    private void checkExistsWorkplaceNameInLocation(String name, Long locationId, Long workplaceId) {
        boolean exists = workplaceId == null
                ? workplaceRepository.existsByNameAndLocationId(name, locationId)
                : workplaceRepository.existsByNameAndLocationIdAndIdNot(name, locationId, workplaceId);

        if (exists) {
            throw new EntityConflictException(
                    "Workplace with name %s in location with id %d already exists".formatted(name, locationId),
                    "error.workplace.name.in_location_already_exists",
                    new Object[]{name, locationId}
            );
        }
    }

    /**
     * Проверяет корректность конфигурации рабочего места.
     *
     * @param workplaceCreationDto данные создаваемого рабочего места
     *
     * @throws InvalidWorkplaceConfigurationException если конфигурация рабочего
     * места не соответствует конфигурации по требованиям типа рабочего места
     */
    private void checkWorkplaceConfiguration(WorkplaceCreationDto workplaceCreationDto) {
        if (workplaceCreationDto.type() == WorkplaceType.INDIVIDUAL) {
            checkIndividualWorkplaceConfiguration(workplaceCreationDto);
        } else {
            checkHotDeskConfiguration(workplaceCreationDto);
        }
    }

    /**
     * Проверяет корректность конфигурации индивидуального рабочего места.
     *
     * @param workplaceCreationDto данные рабочего места
     *
     * @throws InvalidWorkplaceConfigurationException если сотрудник
     * не указан или уже имеет индивидуальное рабочее место
     */
    private void checkIndividualWorkplaceConfiguration(WorkplaceCreationDto workplaceCreationDto) {
        if (workplaceCreationDto.employeeId() == null) {
            throw new InvalidWorkplaceConfigurationException(
                    "An individual workplace should be assigned to an employee",
                    "error.workplace.individual.employee_required"
            );
        }

        checkExistsEmployeeWithIndividualWorkplace(workplaceCreationDto.employeeId(), null);
    }

    /**
     * Проверяет корректность конфигурации рабочего места типа {@link WorkplaceType#HOT_DESK}.
     *
     * @param workplaceCreationDto данные рабочего места
     *
     * @throws InvalidWorkplaceConfigurationException при попытке привязки сотрудника к hot-desk
     */
    private void checkHotDeskConfiguration(WorkplaceCreationDto workplaceCreationDto) {
        if (workplaceCreationDto.employeeId() != null) {
            throw new InvalidWorkplaceConfigurationException(
                    "A Hot desk workplace should not be assigned to an employee",
                    "error.workplace.hot_desk.employee_not_assigned"
            );
        }
    }

    /**
     * Проверяет наличие у сотрудника другого индивидуального рабочего места.
     *
     * <p>При передаче идентификатора рабочего места оно исключается
     * из проверки.</p>
     *
     * @param employeeId идентификатор сотрудника
     * @param workplaceId идентификатор рабочего места, которое необходимо исключить
     *
     * @throws InvalidWorkplaceConfigurationException если сотрудник уже имеет
     * индивидуальное рабочее место
     */
    private void checkExistsEmployeeWithIndividualWorkplace(Long employeeId, Long workplaceId) {
        boolean existsOtherWorkplace = workplaceId == null
                ? workplaceRepository.existsByTypeAndEmployeeId(WorkplaceType.INDIVIDUAL, employeeId)
                : workplaceRepository.existsByTypeAndEmployeeIdAndIdNot(
                        WorkplaceType.INDIVIDUAL, employeeId, workplaceId);

        if (existsOtherWorkplace) {
            throw new InvalidWorkplaceConfigurationException(
                    "The employee has already been assigned an individual workplace",
                    "error.workplace.individual.already_assigned"
            );
        }
    }

    /**
     * Создает исключение о ненайденном рабочем месте.
     *
     * @param id идентификатор рабочего места
     *
     * @return исключение о ненайденном рабочем месте
     */
    private NotFoundEntityException createWorkplaceNotFoundException(Long id) {
        return new NotFoundEntityException(
                "Workplace with id %d not found".formatted(id),
                "error.workplace.not_found",
                new Object[]{id}
        );
    }
}
