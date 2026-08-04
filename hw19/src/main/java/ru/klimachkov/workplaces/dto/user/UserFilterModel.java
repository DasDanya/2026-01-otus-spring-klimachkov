package ru.klimachkov.workplaces.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Параметры фильтрации пользователей")
public record UserFilterModel(
        @Schema(description = "Имя пользователя", example = "Ivanov Ivan")
        String name,
        @Schema(description = "Email пользователя", example = "ivanov_ivan@example.com")
        String email
) {
}
