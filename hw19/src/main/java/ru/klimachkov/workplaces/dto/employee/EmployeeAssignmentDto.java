package ru.klimachkov.workplaces.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Модель для передачи входящих данных о сотруднике")
public record EmployeeAssignmentDto(
        @Schema(description = "ID сотрудника", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{employee.validation.id.notNull}")
        @Positive(message = "{employee.validation.id.positive}")
        Long employeeId
) {
}
