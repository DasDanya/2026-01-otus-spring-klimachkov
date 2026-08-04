package ru.klimachkov.workplaces.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Конфигурационные параметры JWT.
 *
 * <p>Содержит настройки, необходимые для создания и обработки JWT-токенов,
 * включая секретный ключ, время жизни токенов и параметры хранения
 * refresh-токена в cookie.</p>
 *
 * <p>Значения параметров загружаются из конфигурации приложения с префиксом {@code app.jwt}.</p>
 *
 * @see ConfigurationProperties
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * Секретный ключ для подписи и проверки JWT-токенов
     */
    private String secret;

    /**
     * Время жизни access-токена в миллисекундах
     */
    private long accessTokenExpiration;

    /**
     * Время жизни refresh-токена в миллисекундах
     */
    private long refreshTokenExpiration;

    /**
     * Максимальное время жизни refresh-токен cookie в секундах
     */
    private int refreshCookieMaxAge;

    /**
     * Путь, для которого доступна cookie с refresh-токеном.
     */
    private String refreshCookiePath;
}
