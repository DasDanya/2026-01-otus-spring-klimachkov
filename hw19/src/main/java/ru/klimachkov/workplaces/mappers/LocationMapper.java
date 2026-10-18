package ru.klimachkov.workplaces.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.klimachkov.workplaces.dto.location.LocationResponseDto;
import ru.klimachkov.workplaces.models.Location;

/**
 * Маппер для преобразования сущности локации в DTO представление.
 *
 * <p>Используется для преобразования объектов {@link Location} в {@link LocationResponseDto}</p>
 *
 * <p>Реализация маппера генерируется библиотекой MapStruct и регистрируется в Spring-контексте как компонент.</p>
 *
 * @see Location
 * @see LocationResponseDto
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {

    /**
     * Преобразует сущность локации в DTO представление.
     *
     * @param location сущность локации
     * @return DTO локации для передачи клиенту
     */
    LocationResponseDto toResponseDto(Location location);
}
