package ru.klimachkov.workplaces.exceptions;

/**
 * Исключение, возникающее при отсутствии прав доступа к бронированию.
 *
 * <p>Является частным случаем {@link EntityAccessDeniedException} и используется
 * для обработки ситуаций, когда пользователь пытается выполнить операцию
 * с бронированием, но не имеет необходимых прав доступа.</p>
 *
 * <p>Сообщение исключения поддерживает локализацию через {@link MessageSourceException}
 * с использованием кода сообщения из ресурсов.</p>
 *
 * @see EntityAccessDeniedException
 */
public class ReservationAccessDeniedException extends EntityAccessDeniedException {

    /**
     * Создает исключение с указанным сообщением и кодом локализации.
     *
     * @param message стандартное сообщение исключения
     * @param code код сообщения из файла локализации
     */
    public ReservationAccessDeniedException(String message, String code) {
        super(message, code);
    }

}
