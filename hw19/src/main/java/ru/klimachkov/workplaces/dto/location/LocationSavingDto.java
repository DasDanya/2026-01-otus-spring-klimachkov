package ru.klimachkov.workplaces.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для сохранения локации")
public record LocationSavingDto(
        @Schema(
                description = "Название локации. Должно быть уникальным",
                example = "Отдел продаж",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{location.validation.name.notBlank}")
        @Size(max = 100, message = "{location.validation.name.size}")
        String name
) {
}
