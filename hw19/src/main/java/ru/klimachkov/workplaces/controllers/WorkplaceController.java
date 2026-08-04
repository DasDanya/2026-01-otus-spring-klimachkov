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
import ru.klimachkov.workplaces.dto.employee.EmployeeAssignmentDto;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceCreationDto;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceFilterModel;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceRenameDto;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceResponseDto;
import ru.klimachkov.workplaces.services.WorkplaceService;

@RestController
@RequestMapping("/api/workplaces")
@RequiredArgsConstructor
@Tag(name = "WorkplaceController", description = "API для работы с рабочими местами")
@SecurityRequirement(name = "Bearer Authentication")
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    @GetMapping
    @Operation(
            summary = "Получение рабочих мест",
            description = "Возвращает список рабочих мест с учётом фильтрации и пагинации"
    )
    public PagedModel<WorkplaceResponseDto> getAll(
            @ParameterObject @ModelAttribute WorkplaceFilterModel workplaceFilterModel,
            @ParameterObject Pageable pageable
    ) {
        return workplaceService.findAll(workplaceFilterModel, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение рабочего места по его идентификатору",
            description = "Возвращает рабочее место по его идентификатору"
    )
    public WorkplaceResponseDto get(@Parameter(description = "ID рабочего места", example = "1")
                                    @PathVariable Long id
    ) {
        return workplaceService.findByIdWithRelations(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создание рабочего места",
            description = """
                    Создает рабочее место.
                    
                    ### Ограничения:
                    
                    - Название рабочего места должно быть уникальным среди других рабочих мест в локации
                    - Для создания индивидуального рабочего места нужно указать сотрудника, при этом он не должен быть
                      закреплен за другим рабочим местом
                    - Нельзя указать сотрудника при создании хот-деск
                    
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    @ApiResponse(responseCode = "201")
    public WorkplaceResponseDto create(@Valid @RequestBody WorkplaceCreationDto workplaceCreationDto) {
        return workplaceService.create(workplaceCreationDto);
    }

    @PatchMapping("/{id}/rename")
    @Operation(
            summary = "Переименование рабочего места",
            description = """
                    Переименовывает рабочее место.
                    
                    Наименование должно совпадать с исходным или быть уникальным среди рабочих мест в локации
                    
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    public WorkplaceResponseDto rename(
            @Parameter(description = "ID рабочего места", example = "1") @PathVariable Long id,
            @Valid @RequestBody WorkplaceRenameDto workplaceRenameDto
    ) {
        return workplaceService.rename(id, workplaceRenameDto.name());
    }

    @PatchMapping("/{id}/to-hot-desk")
    @Operation(
            summary = "Преобразование рабочего места в хот-деск",
            description = "Преобразует рабочее место в хот-деск. Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN"
    )
    public WorkplaceResponseDto convertToHotDesk(@Parameter(description = "ID рабочего места", example = "1")
                                                 @PathVariable Long id
    ) {
        return workplaceService.convertToHotDesk(id);
    }

    @PatchMapping("/{id}/to-individual")
    @Operation(
            summary = "Преобразование рабочего места в индивидуальное",
            description = "Преобразует рабочее место в индивидуальное. " +
                    "Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN"
    )
    public WorkplaceResponseDto convertToIndividualWorkplace(
            @Parameter(description = "ID рабочего места", example = "1") @PathVariable Long id,
            @Valid @RequestBody EmployeeAssignmentDto employeeAssignmentDto
    ) {
        return workplaceService.convertToIndividual(id, employeeAssignmentDto.employeeId());
    }

    @PatchMapping("/{id}/replace-employee")
    @Operation(
            summary = "Замена сотрудника в рабочем месте",
            description = """
                    Заменяет сотрудника в рабочем месте.
                    
                    Замена происходит только у индивидуального рабочего места.
                    Переданный сотрудник должен быть исходным или не быть привязанным к другому индивидуальному
                    рабочему месту.
                    
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    public WorkplaceResponseDto replaceEmployee(
            @Parameter(description = "ID рабочего места", example = "1") @PathVariable Long id,
            @Valid @RequestBody EmployeeAssignmentDto employeeAssignmentDto
    ) {
        return workplaceService.replaceEmployee(id, employeeAssignmentDto.employeeId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление рабочего места по идентификатору",
            description = """
                    Удаляет рабочее место по его идентификатору.
                    Вместе с рабочим местом удаляются его бронирования.
                    
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    @ApiResponse(responseCode = "204")
    public void delete(@Parameter(description = "ID рабочего места", example = "1") @PathVariable Long id) {
        workplaceService.delete(id);
    }

    @DeleteMapping("/location/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление всех рабочих мест локации",
            description = """
                    Удаляет все рабочие места локации.
                    Вместе с рабочим местом удаляются его бронирования.
                    
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    @ApiResponse(responseCode = "204")
    public void deleteByLocation(@Parameter(description = "ID локации", example = "1")
                                 @PathVariable Long locationId
    ) {
        workplaceService.deleteByLocation(locationId);
    }

    @DeleteMapping("/employee/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление всех рабочих мест сотрудника",
            description = """
                    Удаляет все рабочие места сотрудника (максимум одно индивидуальное).
                    Вместе с рабочим местом удаляются его бронирования.
                    
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    @ApiResponse(responseCode = "204")
    public void deleteByEmployee(
            @Parameter(description = "ID сотрудника", example = "1")
            @PathVariable Long employeeId
    ) {
        workplaceService.deleteByEmployee(employeeId);
    }
}
