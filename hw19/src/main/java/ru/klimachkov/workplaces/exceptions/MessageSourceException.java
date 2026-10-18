package ru.klimachkov.workplaces.exceptions;

import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;

import java.util.Locale;

/**
 * Базовое исключение для бизнес-ошибок с поддержкой локализации сообщений.
 *
 * <p>Используется как родительское исключение для бизнес-исключений,
 * сообщения которых должны быть получены из ресурсов
 * через {@link MessageSource}.</p>
 *
 * <p>Хранит код сообщения и параметры, необходимые для формирования
 * локализованного текста ошибки в зависимости от текущей локали.</p>
 *
 * @see MessageSource
 * @see RuntimeException
 */
public class MessageSourceException extends RuntimeException {

    /**
     * Код сообщения из файла локализации
     */
    private final String code;

    /**
     * Параметры для подстановки в сообщение
     */
    private final Object[] args;

    /**
     * Создает исключение с указанным кодом локализованного сообщения
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     */
    public MessageSourceException(String message, @NonNull String code) {
        this(message, code, null);
    }

    /**
     * Создает исключение с указанным кодом локализованного сообщения
     * и параметрами для его формирования.
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     * @param args параметры для подстановки в сообщение
     */
    public MessageSourceException(String message, @NonNull String code, Object[] args) {
        super(message);
        this.code = code;
        this.args = args;
    }

    /**
     * Получение локализованного сообщения исключения
     *
     * @param messageSource источник сообщений
     * @param locale локаль
     * @return локализованный текст ошибки
     */
    public String getMessage(@NonNull MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(code, args, locale);
    }
}
