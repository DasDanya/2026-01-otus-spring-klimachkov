package ru.klimachkov.workplaces.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.klimachkov.workplaces.models.WorkplaceType;

import java.time.LocalDateTime;

@Schema(description = "Параметры фильтрации бронирований")
public record ReservationFilterModel(
        @Schema(
                description = """
                        Начало общего временного диапазона фильтрации бронирований.
                        Используется совместно с rangeTo.
                        """,
                example = "2026-08-05T10:00:00"
        )
        LocalDateTime rangeFrom,
        @Schema(
                description = """
                        Конец общего временного диапазона фильтрации бронирований.
                        Используется совместно с rangeFrom.
                        """,
                example = "2026-08-05T18:00:00"
        )
        LocalDateTime rangeTo,
        @Schema(
                description = "Минимальное значение даты и времени начала бронирования",
                example = "2026-08-01T09:00:00"
        )
        LocalDateTime startTimeFrom,
        @Schema(
                description = "Максимальное значение даты и времени начала бронирования",
                example = "2026-08-02T09:00:00"
        )
        LocalDateTime startTimeTo,
        @Schema(
                description = "Минимальное значение даты и времени окончания бронирования",
                example = "2026-08-01T10:00:00"
        )
        LocalDateTime endTimeFrom,
        @Schema(
                description = "Максимальное значение даты и времени окончания бронирования",
                example = "2026-08-31T19:00:00"
        )
        LocalDateTime endTimeTo,

        @Schema(description = "ID рабочего места", example = "1")
        Long workplaceId,
        @Schema(description = "Название рабочего места", example = "OH1")
        String workplaceName,
        @Schema(description = "Тип рабочего места", example = "HOT_DESK")
        WorkplaceType workplaceType,

        @Schema(description = "Идентификатор сотрудника", example = "1")
        Long employeeId,
        @Schema(description = "Фамилия сотрудника", example = "Иванов")
        String employeeLastName
) {
}
