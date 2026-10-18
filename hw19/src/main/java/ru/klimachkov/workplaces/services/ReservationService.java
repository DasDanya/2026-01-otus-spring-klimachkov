package ru.klimachkov.workplaces.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import ru.klimachkov.workplaces.dto.reservation.ReservationCreationDto;
import ru.klimachkov.workplaces.dto.reservation.ReservationFilterModel;
import ru.klimachkov.workplaces.dto.reservation.ReservationResponseDto;
import ru.klimachkov.workplaces.dto.reservation.ReservationUpdateDto;

/**
 * Сервис для работы с бронированиями рабочих мест.
 *
 * <p>Предоставляет операции для получения, создания, обновления
 * и удаления бронирований, а также поиска с учетом фильтрации
 * и пагинации.</p>
 */
public interface ReservationService {

    @NonNull
    ReservationResponseDto findById(Long id);

    PagedModel<ReservationResponseDto> findAll(ReservationFilterModel reservationFilterModel, Pageable pageable);

    ReservationResponseDto create(ReservationCreationDto reservationCreationDto);

    ReservationResponseDto update(Long id, ReservationUpdateDto reservationUpdateDto);

    void delete(Long id);

    void deleteByWorkplace(Long workplaceId);

    void deleteByEmployee(Long employeeId);
}
