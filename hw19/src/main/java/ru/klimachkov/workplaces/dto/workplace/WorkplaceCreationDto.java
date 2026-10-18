package ru.klimachkov.workplaces.dto.workplace;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.klimachkov.workplaces.models.WorkplaceType;
import ru.klimachkov.workplaces.validation.annotations.workplace.WorkplaceNameValidation;

@Schema(description = "Данные для создания рабочего места")
public record WorkplaceCreationDto(
        @Schema(
                description = "Название рабочего места",
                example = "HH1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @WorkplaceNameValidation
        String name,

        @Schema(
                description = "Тип рабочего места",
                example = "INDIVIDUAL",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "{workplace.validation.type.notNull}")
        WorkplaceType type,

        @Schema(
                description = "ID локации",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "{workplace.validation.location.id.notNull}")
        @Positive(message = "{workplace.validation.location.id.positive}")
        Long locationId,

        @Schema(
                description = "ID сотрудника",
                example = "1"
        )
        @Positive(message = "{employee.validation.id.positive}")
        Long employeeId
) {
}
