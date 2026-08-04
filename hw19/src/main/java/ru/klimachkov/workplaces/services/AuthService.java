package ru.klimachkov.workplaces.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klimachkov.workplaces.dto.user.UserCredentialsDto;
import ru.klimachkov.workplaces.dto.user.UserRegistrationDto;
import ru.klimachkov.workplaces.dto.user.UserResponseDto;
import ru.klimachkov.workplaces.dto.AccessTokenResponseDto;

/**
 * Сервис для аутентификации и регистрации пользователей.
 *
 * <p>Предоставляет операции для создания новых учетных записей,
 * проверки учетных данных пользователя и выдачи access-токенов.</p>
 *
 * <p>Также отвечает за обновление access-токена на основе refresh-токена,
 * переданного в HTTP-запросе.</p>
 *
 */
public interface AuthService {

    UserResponseDto register(UserRegistrationDto userRegistrationDto);

    AccessTokenResponseDto authenticate(UserCredentialsDto userCredentialsDto, HttpServletResponse response);

    AccessTokenResponseDto refreshAccessToken(HttpServletRequest request);
}
