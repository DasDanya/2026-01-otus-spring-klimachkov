package ru.klimachkov.workplaces.services;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.workplaces.dsl.predicates.EmployeePredicateBuilder;
import ru.klimachkov.workplaces.dto.employee.EmployeeFilterModel;
import ru.klimachkov.workplaces.dto.employee.EmployeeCreationDto;
import ru.klimachkov.workplaces.dto.employee.EmployeeResponseDto;
import ru.klimachkov.workplaces.dto.employee.EmployeeUpdateDto;
import ru.klimachkov.workplaces.exceptions.EntityConflictException;
import ru.klimachkov.workplaces.exceptions.NotFoundEntityException;
import ru.klimachkov.workplaces.mappers.EmployeeMapper;
import ru.klimachkov.workplaces.models.Employee;
import ru.klimachkov.workplaces.models.User;
import ru.klimachkov.workplaces.models.WorkplaceType;
import ru.klimachkov.workplaces.repositories.EmployeeRepository;
import ru.klimachkov.workplaces.repositories.WorkplaceRepository;

/**
 * Реализация сервиса {@link EmployeeService} для работы с данными о сотрудниках.
 *
 * @see EmployeeRepository
 * @see WorkplaceRepository
 * @see EmployeeMapper
 * @see EmployeePredicateBuilder
 * @see UserService
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    /**
     * Репозиторий для работы с сотрудниками
     */
    private final EmployeeRepository employeeRepository;

    /**
     * Репозиторий для работы с рабочими местами
     */
    private final WorkplaceRepository workplaceRepository;

    /**
     * Маппер для преобразования {@link Employee} в DTO представление
     */
    private final EmployeeMapper employeeMapper;

    /**
     * Билдер предиката для формирования условий фильтрации сотрудников.
     */
    private final EmployeePredicateBuilder employeePredicateBuilder;

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Возвращает сотрудника по его идентификатору.
     *
     * @param id идентификатор сотрудника
     * @return найденный сотрудник
     * @throws NotFoundEntityException если сотрудник не найден
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(
                        "Employee with id %d not found".formatted(id),
                        "error.employee.not_found",
                        new Object[]{id})
                );
    }

    /**
     * Возвращает идентификатор сотрудника по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return идентификатор сотрудника
     * @throws NotFoundEntityException если сотрудник не найден
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public Long getIdByUserId(Long userId) {
        return employeeRepository.findIdByUserId(userId)
                .orElseThrow(() -> new NotFoundEntityException(
                        "Employee by user ID %d not found".formatted(userId),
                        "error.employee.not_found_by_user_id",
                        new Object[]{userId})
                );
    }

    /**
     * Возвращает DTO представление сотрудника по его идентификатору.
     *
     * @param id идентификатор сотрудника
     * @return DTO представление сотрудника
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getDtoById(Long id) {
        Employee employee = findById(id);
        return employeeMapper.toResponseDto(employee);
    }

    /**
     * Выполняет поиск сотрудников с учетом фильтрации и пагинации.
     *
     * <p>Фильтрация осуществляется с использованием {@link EmployeePredicateBuilder}.</p>
     *
     * @param employeeFilterModel модель с параметрами фильтрации сотрудников
     * @param pageable параметры пагинации
     * @return страница DTO представлений сотрудников
     */
    @Override
    @Transactional(readOnly = true)
    public PagedModel<EmployeeResponseDto> findAll(EmployeeFilterModel employeeFilterModel, Pageable pageable) {
        Predicate predicate = employeePredicateBuilder.build(employeeFilterModel);
        Page<EmployeeResponseDto> employeesPage = employeeRepository.findAll(predicate, pageable)
                .map(employeeMapper::toResponseDto);

        return new PagedModel<>(employeesPage);
    }

    /**
     * Создает нового сотрудника.
     *
     * <p>Проверяет, что указанный пользователь существует и не связан
     * с другим сотрудником.</p>
     *
     * @param employeeCreationDto данные для создания сотрудника
     * @return DTO представление созданного сотрудника
     *
     * @throws EntityConflictException если пользователь уже связан с сотрудником
     */
    @Override
    @Transactional
    public EmployeeResponseDto create(EmployeeCreationDto employeeCreationDto) {
        User user = userService.findById(employeeCreationDto.userId());

        if (employeeRepository.existsByUserId(employeeCreationDto.userId())) {
            throw new EntityConflictException(
                    "User with id=%d already has an associated employee".formatted(employeeCreationDto.userId()),
                    "error.employee.user.already_associated",
                    new Object[]{employeeCreationDto.userId()}
            );
        }

        Employee employee = Employee.builder()
                .firstname(employeeCreationDto.firstname())
                .lastname(employeeCreationDto.lastname())
                .middlename(employeeCreationDto.middlename())
                .user(user)
                .build();

        Employee insertedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(insertedEmployee);
    }

    /**
     * Обновляет данные сотрудника.
     *
     * @param id идентификатор сотрудника
     * @param employeeUpdateDto новые данные сотрудника
     * @return DTO представление с обновленными данными сотрудника
     *
     * @throws NotFoundEntityException если сотрудник не найден
     */
    @Override
    @Transactional
    public EmployeeResponseDto update(Long id, EmployeeUpdateDto employeeUpdateDto) {
        Employee employee = findById(id);

        employee.setLastname(employeeUpdateDto.lastname());
        employee.setFirstname(employeeUpdateDto.firstname());
        employee.setMiddlename(employeeUpdateDto.middlename());

        return employeeMapper.toResponseDto(employee);
    }

    /**
     * Удаляет сотрудника по идентификатору.
     *
     * <p>Если за сотрудником закреплено рабочее место,
     * то оно становится {@link WorkplaceType#HOT_DESK}
     * и отвязывается от сотрудника.</p>
     *
     * @param id идентификатор сотрудника
     */
    @Override
    @Transactional
    public void delete(Long id) {
        workplaceRepository.findByEmployeeId(id)
                .ifPresent(workplace -> { // Если существует рабочее место, то оно индивидуальное. Делаем его хот-деск
                    workplace.setEmployee(null);
                    workplace.setType(WorkplaceType.HOT_DESK);
                });

        employeeRepository.deleteById(id);
    }
}
