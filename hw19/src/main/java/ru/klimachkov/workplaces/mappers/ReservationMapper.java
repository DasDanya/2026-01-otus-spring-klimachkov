package ru.klimachkov.workplaces.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.klimachkov.workplaces.dto.reservation.ReservationResponseDto;
import ru.klimachkov.workplaces.models.Reservation;

/**
 * Маппер для преобразования сущности бронирования в DTO представление.
 *
 * <p>Используется для преобразования объектов {@link Reservation} в {@link ReservationResponseDto}</p>
 *
 * <p>Для преобразования связанных объектов рабочего места и сотрудника
 * используются {@link WorkplaceMapper} и {@link EmployeeMapper}.</p>
 *
 * <p>Реализация маппера генерируется библиотекой MapStruct и регистрируется в Spring-контексте как компонент.</p>
 *
 * @see Reservation
 * @see ReservationResponseDto
 * @see WorkplaceMapper
 * @see EmployeeMapper
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {WorkplaceMapper.class, EmployeeMapper.class})
public interface ReservationMapper {

    /**
     * Преобразует сущность бронирования в DTO представление.
     *
     * @param reservation сущность бронирования
     * @return DTO бронирования для передачи клиенту
     */
    ReservationResponseDto toResponseDto(Reservation reservation);
}
