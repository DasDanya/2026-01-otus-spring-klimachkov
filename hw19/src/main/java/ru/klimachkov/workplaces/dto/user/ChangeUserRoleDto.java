package ru.klimachkov.workplaces.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.klimachkov.workplaces.models.UserRole;

@Schema(description = "Данные для изменения роли пользователя")
public record ChangeUserRoleDto(
        @Schema(
                description = "Роль пользователя",
                example = "EMPLOYEE",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "{user.validation.role.notNull}")
        UserRole role
) {
}
