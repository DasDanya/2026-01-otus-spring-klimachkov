package ru.klimachkov.workplaces.dto.workplace;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.klimachkov.workplaces.dto.employee.EmployeeResponseDto;
import ru.klimachkov.workplaces.dto.location.LocationResponseDto;
import ru.klimachkov.workplaces.models.WorkplaceType;

@Schema(description = "Информация о рабочем месте")
public record WorkplaceResponseDto(
        @Schema(description = "ID рабочего места", example = "1")
        Long id,
        @Schema(description = "Название рабочего места", example = "OH1")
        String name,
        @Schema(description = "Тип рабочего места", example = "INDIVIDUAL")
        WorkplaceType type,
        @Schema(description = "Вместимость рабочего места", example = "1")
        int capacity,
        @Schema(description = "Локация")
        LocationResponseDto location,
        @Schema(description = "Сотрудник, закрепленный за рабочим местом")
        EmployeeResponseDto employee
) {
}
