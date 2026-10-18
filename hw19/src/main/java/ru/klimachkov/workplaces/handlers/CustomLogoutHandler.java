package ru.klimachkov.workplaces.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.klimachkov.workplaces.exceptions.security.RefreshTokenMissingException;
import ru.klimachkov.workplaces.security.jwt.JwtService;
import ru.klimachkov.workplaces.services.RefreshTokenService;

/**
 * Обработчик выхода пользователя из системы.
 *
 * <p>Удаляет сохраненный refresh-токен пользователя из хранилища
 * и очищает cookie, содержащую refresh-токен.</p>
 *
 * @see JwtService
 * @see RefreshTokenService
 */
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    /**
     * Сервис для работы с JWT-токенами.
     */
    private final JwtService jwtService;

    /**
     * Сервис для работы с хранилищем refresh-токенов.
     */
    private final RefreshTokenService refreshTokenService;

    /**
     * Выполняет выход пользователя из системы.
     *
     * <p>Извлекает refresh-токен из cookie, удаляет его из хранилища
     * и очищает cookie в HTTP-ответе.</p>
     *
     * @param request HTTP-запрос пользователя
     * @param response HTTP-ответ пользователя
     * @param authentication объект аутентификации пользователя
     *
     * @throws RefreshTokenMissingException если refresh-токен не найден в cookie запроса
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = jwtService.extractRefreshTokenFromCookies(request);
        if (accessToken == null) {
            throw new RefreshTokenMissingException();
        }

        refreshTokenService.deleteByToken(accessToken);
        jwtService.deleteRefreshTokenCookie(response);
    }
}
