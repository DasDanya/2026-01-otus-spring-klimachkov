package ru.klimachkov.workplaces.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.klimachkov.workplaces.dto.employee.EmployeeResponseDto;
import ru.klimachkov.workplaces.models.Employee;

/**
 * Маппер для преобразования сущности сотрудника в DTO представление.
 *
 * <p>Используется для преобразования объектов {@link Employee} в {@link EmployeeResponseDto}</p>
 *
 * <p>Реализация маппера генерируется библиотекой MapStruct и регистрируется в Spring-контексте как компонент.</p>
 *
 * @see Employee
 * @see EmployeeResponseDto
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    /**
     * Преобразует сущность сотрудника в DTO представление.
     *
     * @param employee сущность сотрудника
     * @return DTO сотрудника для передачи клиенту
     */
    EmployeeResponseDto toResponseDto(Employee employee);
}
