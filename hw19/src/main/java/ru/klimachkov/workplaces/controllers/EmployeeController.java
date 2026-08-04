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
import ru.klimachkov.workplaces.dto.employee.EmployeeFilterModel;
import ru.klimachkov.workplaces.dto.employee.EmployeeCreationDto;
import ru.klimachkov.workplaces.dto.employee.EmployeeResponseDto;
import ru.klimachkov.workplaces.dto.employee.EmployeeUpdateDto;
import ru.klimachkov.workplaces.services.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "EmployeeController", description = "API для работы с сотрудниками")
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(
            summary = "Получение сотрудников",
            description = "Возвращает список сотрудников с учётом фильтрации и пагинации"
    )
    public PagedModel<EmployeeResponseDto> getAll(
            @ParameterObject @ModelAttribute EmployeeFilterModel employeeFilterModel,
            @ParameterObject Pageable pageable
    ) {
        return employeeService.findAll(employeeFilterModel, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение сотрудника по его идентификатору",
            description = "Возвращает сотрудника по его идентификатору"
    )
    public EmployeeResponseDto get(@Parameter(description = "ID сотрудника", example = "1") @PathVariable Long id) {
        return employeeService.getDtoById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создание сотрудника",
            description = "Создает нового сотрудника. Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN"
    )
    @ApiResponse(responseCode = "201")
    public EmployeeResponseDto create(@Valid @RequestBody EmployeeCreationDto employeeCreationDto) {
        return employeeService.create(employeeCreationDto);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновление данных о сотруднике",
            description = "Обновляет данные о сотруднике. Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN"
    )
    public EmployeeResponseDto update(
            @Parameter(description = "ID сотрудника", example = "1") @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDto employeeUpdateDto
    ) {
        return employeeService.update(id, employeeUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление сотрудника",
            description = """
                    Удаляет сотрудника.
                    Если у сотрудника есть индивидуальное рабочее место, то оно становится хот-деск.
                    Удаляются все бронирования рабочих мест на данного сотрудника.
                    
                    Требуется одна из ролей: SYSTEM_ADMIN, OFFICE_ADMIN
                    """
    )
    @ApiResponse(responseCode = "204")
    public void delete(@Parameter(description = "ID сотрудника", example = "1") @PathVariable Long id) {
        employeeService.delete(id);
    }
}
