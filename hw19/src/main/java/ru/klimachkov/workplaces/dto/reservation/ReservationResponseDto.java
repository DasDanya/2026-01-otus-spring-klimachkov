package ru.klimachkov.workplaces.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.klimachkov.workplaces.dto.employee.EmployeeResponseDto;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceResponseDto;

import java.time.LocalDateTime;

@Schema(description = "Информация о бронировании рабочего места")
public record ReservationResponseDto(
        @Schema(description = "ID бронирования", example = "1")
        Long id,
        @Schema(description = "Дата и время начала бронирования", example = "2026-08-31T09:00:00")
        LocalDateTime startTime,
        @Schema(description = "Дата и время окончания бронирования", example = "2026-08-31T18:00:00")
        LocalDateTime endTime,
        @Schema(description = "Забронированное рабочее место")
        WorkplaceResponseDto workplace,
        @Schema(description = "Сотрудник, для которого создано бронирование")
        EmployeeResponseDto employee
) {
}
