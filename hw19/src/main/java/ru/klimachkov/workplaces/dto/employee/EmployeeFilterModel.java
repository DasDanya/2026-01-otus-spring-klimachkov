package ru.klimachkov.workplaces.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Параметры фильтрации сотрудников")
public record EmployeeFilterModel(
        @Schema(description = "Имя сотрудника", example = "Иван")
        String firstname,
        @Schema(description = "Фамилия сотрудника", example = "Иванов")
        String lastname,
        @Schema(description = "Отчество сотрудника", example = "Иванович")
        String middlename,
        @Schema(description = "ID пользователя", example = "1")
        Long userId
) {
}