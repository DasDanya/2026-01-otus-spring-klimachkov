package ru.klimachkov.workplaces.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Данные для аутентификации пользователя")
public record UserCredentialsDto(
        @Schema(
                description = "Email пользователя",
                example = "ivanov_ivan@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "{user.validation.email.notBlank}")
        @Email(message = "{user.validation.email.invalid}")
        String email,

        @Schema(
                description = "Пароль пользователя",
                example = "ivanov_ivan1!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "{user.validation.password.notBlank}")
        String password
) {
}
