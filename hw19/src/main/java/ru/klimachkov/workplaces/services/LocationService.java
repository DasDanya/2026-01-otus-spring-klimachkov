package ru.klimachkov.workplaces.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import ru.klimachkov.workplaces.dto.location.LocationFilterModel;
import ru.klimachkov.workplaces.dto.location.LocationSavingDto;
import ru.klimachkov.workplaces.dto.location.LocationResponseDto;
import ru.klimachkov.workplaces.models.Location;

/**
 * Сервис для работы с локациями.
 *
 * <p>Предоставляет операции для получения, создания, обновления
 * и удаления локаций, а также поиска с учетом фильтрации
 * и пагинации.</p>
 *
 */
public interface LocationService {

    @NonNull
    Location findById(Long id);

    @NonNull
    LocationResponseDto getDtoById(Long id);

    PagedModel<LocationResponseDto> findAll(LocationFilterModel locationFilterModel, Pageable pageable);

    LocationResponseDto create(LocationSavingDto locationRequestDto);

    LocationResponseDto update(Long id, String name);

    void delete(Long id);
}
