package ru.klimachkov.workplaces.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.klimachkov.workplaces.dto.user.UserCredentialsDto;
import ru.klimachkov.workplaces.dto.user.UserRegistrationDto;
import ru.klimachkov.workplaces.dto.user.UserResponseDto;
import ru.klimachkov.workplaces.dto.AccessTokenResponseDto;
import ru.klimachkov.workplaces.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "API для регистрации, аутентификации и обновления JWT токенов")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Регистрация пользователя",
            description = "Создаёт нового пользователя системы"
    )
    @ApiResponse(responseCode = "201")
    public UserResponseDto register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        return authService.register(userRegistrationDto);
    }

    @PostMapping("/authenticate")
    @Operation(
            summary = "Аутентификация пользователя",
            description = """
                    Проверяет учетные данные пользователя.
                    
                    В случае успешной аутентификации возвращает JWT access token.
                    Refresh token сохраняется в HttpOnly cookie
                    """
    )
    public AccessTokenResponseDto authenticate(
            @Valid @RequestBody UserCredentialsDto userCredentialsDto,
            HttpServletResponse response
    ) {
        return authService.authenticate(userCredentialsDto, response);
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Обновление JWT access token",
            description = "Из HTTP-only cookie извлекается refresh token, после его валидации генерируется " +
                    "новый JWT access token"
    )
    public AccessTokenResponseDto refreshAccessToken(HttpServletRequest request) {
        return authService.refreshAccessToken(request);
    }
}
