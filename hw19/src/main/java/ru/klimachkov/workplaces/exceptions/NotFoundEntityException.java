package ru.klimachkov.workplaces.exceptions;

/**
 * Исключение, возникающее при отсутствии сущности в системе.
 *
 * <p>Используется для обработки ситуаций, когда требуемая сущность
 * не была найдена при выполнении операции.</p>
 *
 * <p>Сообщение исключения поддерживает локализацию через {@link MessageSourceException}
 * с использованием кода сообщения и параметров из ресурсов.</p>
 *
 * @see MessageSourceException
 */
public class NotFoundEntityException extends MessageSourceException {

    /**
     * Создает исключение с указанным сообщением, кодом локализации
     * и параметрами для формирования сообщения.
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     * @param args параметры для подстановки в сообщение
     */
    public NotFoundEntityException(String message, String code, Object[] args) {
        super(message, code, args);
    }
}
