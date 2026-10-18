package ru.klimachkov.workplaces.exceptions.security;

/**
 * Исключение, возникающее при попытке выполнения операции заблокированным пользователем.
 *
 * @see RuntimeException
 */
public class UserLockedException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением
     *
     * @param message сообщение
     */
    public UserLockedException(String message) {
        super(message);
    }
}
