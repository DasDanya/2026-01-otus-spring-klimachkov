package ru.klimachkov.workplaces.exceptions;

/**
 * Исключение, возникающее при невозможности обработки корректных данных
 * из-за нарушения бизнес-правил системы.
 *
 * <p>Используется для обработки ситуаций, когда переданные данные имеют
 * корректный формат, но операция не может быть выполнена в текущем состоянии
 * системы.</p>
 *
 * <p>Сообщение исключения поддерживает локализацию через {@link MessageSourceException}
 * с использованием кода сообщения из ресурсов.</p>
 *
 * @see MessageSourceException
 */
public class UnprocessableEntityException extends MessageSourceException {

    /**
     * Создает исключение с указанным сообщением и кодом локализации.
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     */
    public UnprocessableEntityException(String message, String code) {
        super(message, code);
    }
}
