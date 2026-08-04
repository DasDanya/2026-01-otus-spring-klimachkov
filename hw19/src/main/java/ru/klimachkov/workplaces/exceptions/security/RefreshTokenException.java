package ru.klimachkov.workplaces.exceptions.security;

/**
 * Исключение, возникающее при ошибке обработки refresh-токена.
 *
 * @see RuntimeException
 */
public class RefreshTokenException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением
     *
     * @param message сообщение
     */
    public RefreshTokenException(String message) {
        super(message);
    }
}
