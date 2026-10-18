package ru.klimachkov.workplaces.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Сервис для работы с JWT-токенами.
 *
 * <p>Предоставляет операции для создания, извлечения и проверки
 * JWT-токенов, а также управления refresh-токенами, хранящимися
 * в cookie HTTP-ответа.</p>
 *
 * <p>Используется для обеспечения аутентификации пользователей
 * и управления жизненным циклом access и refresh токенов.</p>
 */
public interface JwtService {

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String extractToken(HttpServletRequest request);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isValidRefreshToken(String token, UserDetails userDetails);

    String extractUsername(String token);

    void addRefreshTokenCookie(HttpServletResponse response, String refreshToken);

    String extractRefreshTokenFromCookies(HttpServletRequest request);

    void deleteRefreshTokenCookie(HttpServletResponse response);
}
