package ru.klimachkov.workplaces.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.klimachkov.workplaces.dto.location.LocationFilterModel;
import ru.klimachkov.workplaces.dto.location.LocationSavingDto;
import ru.klimachkov.workplaces.dto.location.LocationResponseDto;
import ru.klimachkov.workplaces.services.LocationService;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "LocationController", description = "API для работы с локациями")
@SecurityRequirement(name = "Bearer Authentication")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    @Operation(
            summary = "Получение локаций",
            description = "Возвращает список локаций с учётом фильтрации и пагинации"
    )
    public PagedModel<LocationResponseDto> getAll(
            @ParameterObject @ModelAttribute LocationFilterModel locationFilterModel,
            @ParameterObject Pageable pageable
    ) {
        return locationService.findAll(locationFilterModel, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение локации по её идентификатору",
            description = "Возвращает локацию по её идентификатору"
    )
    public LocationResponseDto get(@Parameter(description = "ID локации", example = "1") @PathVariable Long id) {
        return locationService.getDtoById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создание локации",
            description = "Создает новую локацию. Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN"
    )
    @ApiResponse(responseCode = "201")
    public LocationResponseDto create(@Valid @RequestBody LocationSavingDto locationSavingDto) {
        return locationService.create(locationSavingDto);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновление данных о локации",
            description = "Обновляет данные о локации. Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN"
    )
    public LocationResponseDto update(
            @Parameter(description = "ID локации", example = "1") @PathVariable long id,
            @Valid @RequestBody LocationSavingDto locationSavingDto
    ) {
        return locationService.update(id, locationSavingDto.name());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление локации",
            description = """
                    Удаляет локацию.
                    Вместе с локацией удаляются все её рабочие места
                    
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    @ApiResponse(responseCode = "204")
    public void delete(@PathVariable Long id) {
        locationService.delete(id);
    }
}
