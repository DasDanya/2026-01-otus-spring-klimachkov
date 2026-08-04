package ru.klimachkov.workplaces.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import ru.klimachkov.workplaces.validation.annotations.reservation.ReservationEmployeeIdValidation;
import ru.klimachkov.workplaces.validation.annotations.reservation.ReservationEndTimeValidation;
import ru.klimachkov.workplaces.validation.annotations.reservation.ReservationStartTimeValidation;

import java.time.LocalDateTime;

@Schema(description = "Данные для обновления бронирования")
public record ReservationUpdateDto(
        @Schema(description = "ID сотрудника", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @ReservationEmployeeIdValidation
        Long employeeId,

        @Schema(
                description = "Дата и время начала бронирования",
                example = "2026-08-25T10:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @ReservationStartTimeValidation
        LocalDateTime startTime,

        @Schema(
                description = "Дата и время окончания бронирования",
                example = "2026-08-25T18:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @ReservationEndTimeValidation
        LocalDateTime endTime
) {
}
