package ru.klimachkov.workplaces.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.klimachkov.workplaces.dto.user.ChangeUserRoleDto;
import ru.klimachkov.workplaces.dto.user.UserFilterModel;
import ru.klimachkov.workplaces.dto.user.UserResponseDto;
import ru.klimachkov.workplaces.services.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "API для работы с пользователями")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Получение пользователей",
            description = "Возвращает список пользователей с учётом фильтрации и пагинации. Требуемая роль SYSTEM_ADMIN"
    )
    public PagedModel<UserResponseDto> getAll(
            @ParameterObject @ModelAttribute UserFilterModel userFilterModel,
            @ParameterObject Pageable pageable
    ) {
        return userService.findAll(userFilterModel, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение пользователя по его идентификатору",
            description = "Возвращает пользователя по его идентификатору. Требуемая роль SYSTEM_ADMIN"
    )
    public UserResponseDto get(@Parameter(description = "ID пользователя", example = "1") @PathVariable Long id) {
        return userService.getDtoById(id);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Получение текущего пользователя",
            description = "Возвращает данные текущего авторизованного пользователя"
    )
    public UserResponseDto getMe() {
        return userService.getMeDto();
    }

    @PatchMapping("/{id}/change-role")
    @Operation(
            summary = "Изменение роли пользователя",
            description = "Изменяет роль пользователя. Требуется роль SYSTEM_ADMIN"
    )
    public UserResponseDto changeRole(
            @Parameter(description = "ID пользователя", example = "2") @PathVariable Long id,
            @Valid @RequestBody ChangeUserRoleDto changeUserRoleDto
    ) {
        return userService.changeRole(id, changeUserRoleDto.role());
    }

    @PatchMapping("/{id}/lock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Блокировка пользователя",
            description = "Блокирует пользователя. Требуется роль SYSTEM_ADMIN"
    )
    @ApiResponse(responseCode = "204")
    public void lock(@Parameter(description = "ID пользователя", example = "2") @PathVariable Long id) {
        userService.lock(id);
    }

    @PatchMapping("/{id}/unlock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Разблокировка пользователя",
            description = "Разблокирует пользователя. Требуется роль SYSTEM_ADMIN"
    )
    @ApiResponse(responseCode = "204")
    public void unlock(@Parameter(description = "ID пользователя", example = "2") @PathVariable Long id) {
        userService.unlock(id);
    }
}
