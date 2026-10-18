package ru.klimachkov.workplaces.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.klimachkov.workplaces.validation.annotations.employee.EmployeeFirstnameValidation;
import ru.klimachkov.workplaces.validation.annotations.employee.EmployeeLastnameValidation;
import ru.klimachkov.workplaces.validation.annotations.employee.EmployeeMiddlenameValidation;

@Schema(description = "Данные для обновления данных сотрудника")
public record EmployeeUpdateDto (

        @Schema(description = "Имя сотрудника", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
        @EmployeeFirstnameValidation
        String firstname,

        @Schema(description = "Фамилия сотрудника", example = "Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
        @EmployeeLastnameValidation
        String lastname,

        @Schema(description = "Отчество сотрудника", example = "Иванович")
        @EmployeeMiddlenameValidation
        String middlename
) {
}
