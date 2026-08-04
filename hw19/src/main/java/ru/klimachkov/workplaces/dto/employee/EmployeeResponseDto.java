package ru.klimachkov.workplaces.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о сотруднике")
public record EmployeeResponseDto(
        @Schema(description = "ID сотрудника", example = "1")
        Long id,
        @Schema(description = "Имя сотрудника", example = "Иван")
        String firstname,
        @Schema(description = "Фамилия сотрудника", example = "Иванов")
        String lastname,
        @Schema(description = "Отчество сотрудника", example = "Иванович")
        String middlename
) {
}
