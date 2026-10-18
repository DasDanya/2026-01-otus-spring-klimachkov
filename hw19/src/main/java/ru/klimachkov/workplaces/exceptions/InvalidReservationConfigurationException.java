package ru.klimachkov.workplaces.exceptions;

/**
 * Исключение, возникающее при некорректной конфигурации бронирования.
 *
 * <p>Является частным случаем {@link UnprocessableEntityException} и используется
 * для обработки ситуаций, когда параметры бронирования не соответствуют
 * бизнес-правилам системы.</p>
 *
 * <p>Сообщение исключения поддерживает локализацию через {@link MessageSourceException}
 * с использованием кода сообщения из ресурсов</p>
 *
 * @see UnprocessableEntityException
 */
public class InvalidReservationConfigurationException extends UnprocessableEntityException {

    /**
     * Создает исключение с указанным сообщением и кодом локализации
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     */
    public InvalidReservationConfigurationException(String message, String code) {
        super(message, code);
    }
}
