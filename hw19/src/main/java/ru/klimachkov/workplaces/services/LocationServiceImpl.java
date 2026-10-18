package ru.klimachkov.workplaces.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.workplaces.dto.location.LocationFilterModel;
import ru.klimachkov.workplaces.dto.location.LocationSavingDto;
import ru.klimachkov.workplaces.dto.location.LocationResponseDto;
import ru.klimachkov.workplaces.exceptions.EntityConflictException;
import ru.klimachkov.workplaces.exceptions.NotFoundEntityException;
import ru.klimachkov.workplaces.mappers.LocationMapper;
import ru.klimachkov.workplaces.models.Location;
import ru.klimachkov.workplaces.repositories.LocationRepository;

/**
 * Реализация сервиса {@link LocationService} для работы с данными о локациях.
 *
 * @see LocationRepository
 * @see LocationMapper
 */
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    /**
     * Репозиторий для работы с локациями
     */
    private final LocationRepository locationRepository;

    /**
     * Маппер для преобразования {@link Location} в DTO представление.
     */
    private final LocationMapper locationMapper;

    /**
     * Возвращает локацию по её идентификатору.
     *
     * @param id идентификатор локации
     * @return найденная локация
     * @throws NotFoundEntityException если локация не найдена
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public Location findById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(
                        "Location with id %d not found".formatted(id),
                        "error.location.not_found",
                        new Object[]{id}
                ));
    }

    /**
     * Возвращает DTO представление локации по её идентификатору.
     *
     * @param id идентификатор локации
     * @return DTO представление локации
     */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public LocationResponseDto getDtoById(Long id) {
        Location location = findById(id);
        return locationMapper.toResponseDto(location);
    }

    /**
     * Выполняет поиск локаций с учетом фильтрации и пагинации.
     *
     * <p>Если задан параметр имени, используется поиск по частичному совпадению,
     * иначе возвращаются все локации.</p>
     *
     * @param locationFilterModel модель с параметрами фильтрации
     * @param pageable параметры пагинации
     * @return страница DTO представлений локаций
     */

    @Override
    @Transactional(readOnly = true)
    public PagedModel<LocationResponseDto> findAll(LocationFilterModel locationFilterModel, Pageable pageable) {
        Page<Location> locations = locationFilterModel.name() != null && !locationFilterModel.name().isBlank()
                ? locationRepository.findByNameContainingIgnoreCase(locationFilterModel.name(), pageable)
                : locationRepository.findAll(pageable);

        return new PagedModel<>(locations.map(locationMapper::toResponseDto));
    }

    /**
     * Создает новую локацию.
     *
     * <p>Перед созданием выполняется проверка уникальности имени локации.</p>
     *
     * @param locationRequestDto данные для создания локации
     * @return DTO представление созданной локации
     *
     * @throws EntityConflictException если локация с таким именем уже существует
     */
    @Override
    @Transactional
    public LocationResponseDto create(LocationSavingDto locationRequestDto) {
        checkExistsLocationName(locationRequestDto.name(), null);

        Location location = new Location();
        location.setName(locationRequestDto.name());

        Location createdLocation = locationRepository.save(location);
        return locationMapper.toResponseDto(createdLocation);
    }

    /**
     * Обновляет данные локации.
     *
     * <p>Перед обновлением выполняется проверка уникальности имени.</p>
     *
     * @param id идентификатор локации
     * @param name новое имя локации
     * @return DTO представление с обновленными данными локации
     *
     * @throws NotFoundEntityException если локация не найдена
     * @throws EntityConflictException если имя уже используется другой локацией
     */
    @Override
    @Transactional
    public LocationResponseDto update(Long id, String name) {
        Location location = findById(id);

        checkExistsLocationName(name, id);
        location.setName(name);

        return locationMapper.toResponseDto(location);
    }

    /**
     * Удаляет локацию по идентификатору.
     *
     * @param id идентификатор локации
     */
    @Override
    @Transactional
    public void delete(Long id) {
        locationRepository.deleteById(id);
    }

    /**
     * Проверяет, существует ли локация с указанным именем.
     *
     * <p>Если передан идентификатор, то локация с этим идентификатором
     * исключается из проверки.</p>
     *
     * @param name имя локации
     * @param id идентификатор локации (может быть {@code null})
     *
     * @throws EntityConflictException если локация с таким именем уже существует
     */
    private void checkExistsLocationName(String name, Long id) {
        boolean existsLocationName = id == null
                ? locationRepository.existsByName(name)
                : locationRepository.existsByNameAndIdNot(name, id);

        if (existsLocationName) {
            throw new EntityConflictException(
                    "Location with name %s already exists".formatted(name),
                    "error.location.name.already_exists",
                    new Object[]{name}
            );
        }
    }
}
