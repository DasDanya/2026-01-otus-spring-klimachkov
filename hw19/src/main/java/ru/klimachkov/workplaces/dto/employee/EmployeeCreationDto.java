package ru.klimachkov.workplaces.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.klimachkov.workplaces.validation.annotations.employee.EmployeeFirstnameValidation;
import ru.klimachkov.workplaces.validation.annotations.employee.EmployeeLastnameValidation;
import ru.klimachkov.workplaces.validation.annotations.employee.EmployeeMiddlenameValidation;

@Schema(description = "Данные для создания сотрудника")
public record EmployeeCreationDto(

        @Schema(description = "Имя сотрудника", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
        @EmployeeFirstnameValidation
        String firstname,

        @Schema(description = "Фамилия сотрудника", example = "Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
        @EmployeeLastnameValidation
        String lastname,

        @Schema(description = "Отчество сотрудника", example = "Иванович")
        @EmployeeMiddlenameValidation
        String middlename,

        @Schema(description = "ID пользователя", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{employee.validation.user.id.notNull}")
        @Positive(message = "{employee.validation.user.id.positive}")
        Long userId
) {
}
