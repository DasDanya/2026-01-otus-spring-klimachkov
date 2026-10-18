package ru.klimachkov.workplaces.exceptions;

/**
 * Исключение, возникающее при попытке выполнения запрещённой операции.
 *
 * <p>Используется для обработки ситуаций, когда операция не может быть выполнена
 * из-за ограничений бизнес-логики или правил системы.</p>
 *
 * <p>Сообщение исключения поддерживает локализацию через {@link MessageSourceException}
 * с использованием кода сообщения из ресурсов.</p>
 *
 * @see MessageSourceException
 */
public class ForbiddenOperationException extends MessageSourceException {

    /**
     * Создает исключение с указанным сообщением и кодом локализации
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     */

    public ForbiddenOperationException(String message, String code) {
        super(message, code);
    }
}
