package ru.klimachkov.workplaces.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.klimachkov.workplaces.models.UserRole;

@Schema(description = "Информация о пользователе системы")
public record UserResponseDto(
        @Schema(description = "ID пользователя", example = "1")
        long id,
        @Schema(description = "Имя пользователя", example = "Ivanov Ivan")
        String name,
        @Schema(description = "Email пользователя", example = "ivanov_ivan@example.com")
        String email,
        @Schema(description = "Роль пользователя в системе", example = "EMPLOYEE")
        UserRole role,
        @Schema(description = "Признак блокировки пользователя", example = "false")
        boolean locked
) {
}
