package ru.klimachkov.workplaces.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о локации")
public record LocationResponseDto(
        @Schema(description = "ID локации", example = "1")
        long id,
        @Schema(description = "Название локации", example = "Отдел продаж")
        String name
) {
}
