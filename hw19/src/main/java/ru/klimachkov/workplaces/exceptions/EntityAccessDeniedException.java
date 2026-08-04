package ru.klimachkov.workplaces.exceptions;

/**
 * Исключение, возникающее при отсутствии прав доступа к сущности.
 *
 * <p>Используется для обработки ситуаций, когда пользователь пытается выполнить
 * операцию над сущностью, к которой у него отсутствуют необходимые права доступа.</p>
 *
 * <p>Сообщение исключения поддерживает локализацию через {@link MessageSourceException}
 * с использованием кода сообщения из ресурсов.</p>
 *
 * @see MessageSourceException
 */
public class EntityAccessDeniedException extends MessageSourceException {

    /**
     * Создает исключение с указанным сообщением и кодом локализации
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     */
    public EntityAccessDeniedException(String message, String code) {
        super(message, code);
    }
}
