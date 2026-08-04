package ru.klimachkov.workplaces.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceResponseDto;
import ru.klimachkov.workplaces.models.Workplace;

/**
 * Маппер для преобразования сущности рабочего места в DTO представление.
 *
 * <p>Используется для преобразования объектов {@link Workplace} в {@link WorkplaceResponseDto}</p>
 *
 * <p>Для преобразования связанных объектов локации и сотрудника
 * используются {@link LocationMapper} и {@link EmployeeMapper}.</p>
 *
 * <p>Реализация маппера генерируется библиотекой MapStruct и регистрируется в Spring-контексте как компонент.</p>
 *
 * @see Workplace
 * @see WorkplaceResponseDto
 * @see LocationMapper
 * @see EmployeeMapper
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {LocationMapper.class, EmployeeMapper.class})
public interface WorkplaceMapper {

    /**
     * Преобразует сущность рабочего места в DTO представление.
     *
     * @param workplace сущность рабочего места
     * @return DTO рабочего места для передачи клиенту
     */
    WorkplaceResponseDto toResponseDto(Workplace workplace);
}
