package ru.klimachkov.workplaces.dto.workplace;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.klimachkov.workplaces.models.WorkplaceType;

@Schema(description = "Параметры фильтрации рабочих мест")
public record WorkplaceFilterModel(
        @Schema(description = "Название рабочего места", example = "OH1")
        String name,
        @Schema(description = "Тип рабочего места", example = "INDIVIDUAL")
        WorkplaceType type,
        @Schema(description = "ID локации", example = "1")
        Long locationId,
        @Schema(description = "Название локации", example = "Open Space")
        String locationName,
        @Schema(description = "ID сотрудника", example = "1")
        Long employeeId,
        @Schema(description = "Фамилия сотрудника", example = "Иванов")
        String employeeLastName
) {
}
