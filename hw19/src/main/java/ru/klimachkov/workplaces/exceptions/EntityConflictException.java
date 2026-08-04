package ru.klimachkov.workplaces.exceptions;

/**
 * Исключение, возникающее при конфликте состояния сущности
 * с текущими бизнес-правилами системы.
 *
 * <p>Используется для обработки ситуаций, когда выполнение операции невозможно
 * из-за существующего конфликта с данными системы.</p>
 *
 * <p>Сообщение исключения поддерживает локализацию через {@link MessageSourceException}
 * с использованием кода сообщения и параметров из ресурсов.</p>
 *
 * @see MessageSourceException
 */
public class EntityConflictException extends MessageSourceException {

    /**
     * Создает исключение с указанным сообщением и кодом локализации
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     */
    public EntityConflictException(String message, String code, Object[] args) {
        super(message, code, args);
    }
}
