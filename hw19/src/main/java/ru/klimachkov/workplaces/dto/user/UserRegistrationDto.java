package ru.klimachkov.workplaces.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для регистрации нового пользователя")
public record UserRegistrationDto(
        @Schema(
                description = "Имя пользователя",
                example = "Ivanov Ivan",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "{user.validation.name.notBlank}")
        @Size(max = 255, message = "{user.validation.name.size}")
        String name,

        @Schema(
                description = "Email пользователя",
                example = "ivanov_ivan@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "{user.validation.email.notBlank}")
        @Size(max = 254, message = "{user.validation.email.size}")
        @Email(message = "{user.validation.email.invalid}")
        String email,

        @Schema(
                description = """
                        Пароль пользователя.
                        
                        Требования:
                        - от 8 до 255 символов
                        - минимум одна латинская буква
                        - минимум одна цифра
                        - минимум один специальный символ
                        """,
                example = "ivanov_ivan1!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "{user.validation.password.notBlank}")
        @Size(min = 8, max = 255, message = "{user.validation.password.size}")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])[A-Za-z\\d[^A-Za-z\\d\\s]]{8,}$",
                message = "{user.validation.password.pattern}")
        String password
) {
}
