package ru.klimachkov.workplaces.exceptions;

/**
 * Исключение, возникающее при отсутствии пользователя в системе.
 *
 * <p>Используется для обработки ситуаций, когда пользователь не был найден при выполнении операции</p>
 *
 * @see RuntimeException
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением
     *
     * @param message сообщение
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
