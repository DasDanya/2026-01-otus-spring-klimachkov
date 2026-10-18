package ru.klimachkov.workplaces.dto.workplace;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.klimachkov.workplaces.validation.annotations.workplace.WorkplaceNameValidation;

@Schema(description = "Данные для переименования рабочего места")
public record WorkplaceRenameDto(
        @Schema(
                description = "Название рабочего места",
                example = "HH1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @WorkplaceNameValidation
        String name
) {
}
