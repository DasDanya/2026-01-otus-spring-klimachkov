package ru.klimachkov.workplaces.exceptions.security;

/**
 * Исключение, возникающее при отсутствии refresh-токена пользователя.
 *
 * <p>Является частным случаем ошибки обработки {@link RefreshTokenException} и используется
 * для обработки ситуаций, когда для выполнения операции требуется наличие refresh-токена,
 * но он отсутствует.</p>
 *
 * @see RefreshTokenException
 */
public class RefreshTokenMissingException extends RefreshTokenException {

    /**
     * Создает исключение с сообщением об отсутствии refresh-токена.
     */
    public RefreshTokenMissingException() {
        super("Refresh token is missing");
    }
}
