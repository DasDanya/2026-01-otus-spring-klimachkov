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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.klimachkov.workplaces.dto.reservation.ReservationCreationDto;
import ru.klimachkov.workplaces.dto.reservation.ReservationFilterModel;
import ru.klimachkov.workplaces.dto.reservation.ReservationResponseDto;
import ru.klimachkov.workplaces.dto.reservation.ReservationUpdateDto;
import ru.klimachkov.workplaces.services.ReservationService;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "ReservationController", description = "API для работы с бронированием рабочих мест")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(
            summary = "Получение бронирований",
            description = "Возвращает список бронирований с учётом фильтрации и пагинации"
    )
    public PagedModel<ReservationResponseDto> getAll(
            @ParameterObject @ModelAttribute ReservationFilterModel reservationFilterModel,
            @ParameterObject Pageable pageable
    ) {
        return reservationService.findAll(reservationFilterModel, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение бронирования по его идентификатору",
            description = "Возвращает бронирование по его идентификатору"
    )
    public ReservationResponseDto get(@Parameter(description = "ID бронирования", example = "1")
                                      @PathVariable Long id
    ) {
        return reservationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создание бронирования",
            description = """
                    Создает новое бронирование рабочего места.
                    
                    ### Правила доступа:
                    
                    - Для индивидуального рабочего места:
                      - Администратор может создать бронирование для любого сотрудника
                      - Сотрудник может создать бронирование только если он закреплён за данным рабочим местом
                        (является его владельцем), при этом он может забронировать как на себя,
                        так и на любого другого сотрудника
                    
                    - Для рабочего места типа HOT_DESK:
                      - Администратор может создать бронирование для любого сотрудника
                      - Сотрудник может создать бронирование только для себя
                    
                    ### Ограничения по времени:
                    
                    - Время начала должно быть раньше времени окончания
                    - Бронирование не должно пересекаться:
                      - с другими бронированиями этого рабочего места
                      - с другими бронированиями данного сотрудника
                    """
    )
    @ApiResponse(responseCode = "201")
    public ReservationResponseDto create(@Valid @RequestBody ReservationCreationDto reservationCreationDto) {
        return reservationService.create(reservationCreationDto);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновление бронирования",
            description = """
                    Обновляет бронирование рабочего места.
                    
                    ### Правила доступа:
                    
                    - Для индивидуального рабочего места:
                      - Администратор может обновить любое бронирование
                      - Сотрудник может обновить бронирование только если он закреплён за данным рабочим местом
                        (является его владельцем), при этом он может забронировать как на себя,
                        так и на любого другого сотрудника
                    
                    - Для рабочего места типа HOT_DESK:
                      - Администратор может обновить любое бронирование
                      - Сотрудник может обновить только собственное бронирование
                    
                    ### Ограничения по времени:
                    
                    - Время начала должно быть раньше времени окончания
                    - Бронирование не должно пересекаться:
                      - с другими бронированиями этого рабочего места
                      - с другими бронированиями данного сотрудника
                    """
    )
    public ReservationResponseDto update(
            @Parameter(description = "ID бронирования", example = "1") @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateDto reservationUpdateDto
    ) {
        return reservationService.update(id, reservationUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление бронирования",
            description = """
                    Удаляет бронирование.
                    
                    ### Правила доступа:
                    
                    Удалить бронирование может:
                    
                    • Администратор
           
                    • Сотрудник закрепленный за рабочим местом (касается только индивидуальных рабочих мест)
                    
                    • Сотрудник, на которого забронировали рабочее место
                    """
    )
    @ApiResponse(responseCode = "204")
    public void delete(@Parameter(description = "ID бронирования", example = "1") @PathVariable Long id) {
        reservationService.delete(id);
    }

    @DeleteMapping("/workplace/{workplaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление бронирований по идентификатору рабочего места",
            description = """
                    Удаляет все бронирования рабочего места.
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    @ApiResponse(responseCode = "204")
    public void deleteByWorkplace(@Parameter(description = "ID рабочего места", example = "1")
                                  @PathVariable Long workplaceId
    ) {
        reservationService.deleteByWorkplace(workplaceId);
    }

    @DeleteMapping("/employee/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление бронирований по идентификатору сотрудника",
            description = """
                    Удаляет все бронирования сотрудника.
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    @ApiResponse(responseCode = "204")
    public void deleteByEmployee(@Parameter(description = "ID сотрудника", example = "1")
                                 @PathVariable Long employeeId
    ) {
        reservationService.deleteByEmployee(employeeId);
    }
}
