package ru.klimachkov.workplaces.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import ru.klimachkov.workplaces.dto.employee.EmployeeFilterModel;
import ru.klimachkov.workplaces.dto.employee.EmployeeCreationDto;
import ru.klimachkov.workplaces.dto.employee.EmployeeResponseDto;
import ru.klimachkov.workplaces.dto.employee.EmployeeUpdateDto;
import ru.klimachkov.workplaces.models.Employee;

/**
 * Сервис для работы с данными о сотрудниках.
 *
 * <p>Предоставляет операции для получения, создания, обновления
 * и удаления сотрудников, а также поиска с учетом фильтрации
 * и пагинации.</p>
 *
 */
public interface EmployeeService {

    @NonNull
    Employee findById(Long id);

    @NonNull
    Long getIdByUserId(Long userId);

    @NonNull
    EmployeeResponseDto getDtoById(Long id);

    PagedModel<EmployeeResponseDto> findAll(EmployeeFilterModel employeeFilterModel, Pageable pageable);

    EmployeeResponseDto create(EmployeeCreationDto employeeCreationDto);

    EmployeeResponseDto update(Long id, EmployeeUpdateDto employeeUpdateDto);

    void delete(Long id);
}
