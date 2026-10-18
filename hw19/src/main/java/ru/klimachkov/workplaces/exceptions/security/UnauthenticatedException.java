package ru.klimachkov.workplaces.exceptions.security;

/**
 * Исключение, возникающее при попытке выполнения операции неаутентифицированным пользователем.
 *
 * <p>Используется для обработки ситуаций, когда невозможно определить текущего пользователя системы.</p>
 *
 * @see RuntimeException
 */
public class UnauthenticatedException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением
     *
     * @param message сообщение
     */
    public UnauthenticatedException(String message) {
        super(message);
    }
}
