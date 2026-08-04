package ru.klimachkov.workplaces.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.klimachkov.workplaces.validation.annotations.reservation.ReservationEmployeeIdValidation;
import ru.klimachkov.workplaces.validation.annotations.reservation.ReservationEndTimeValidation;
import ru.klimachkov.workplaces.validation.annotations.reservation.ReservationStartTimeValidation;

import java.time.LocalDateTime;

@Schema(description = "Данные для создания бронирования")
public record ReservationCreationDto(

        @Schema(description = "ID рабочего места", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{reservation.validation.workplace.id.notNull}")
        @Positive(message = "{reservation.validation.workplace.id.positive}")
        Long workplaceId,

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
