package ru.klimachkov.hw.integration;

import org.springframework.messaging.Message;
import ru.klimachkov.hw.exceptions.MissingHeaderException;

import java.time.LocalDateTime;

/**
 * Класс для работы с заголовками сообщений заказов.
 * Содержит константы и методы для безопасного извлечения значений.
 * При отсутствии заголовка выбрасывается {@link MissingHeaderException}.
 */
public class OrderHeaders {

    public static final String VALID_ORDERS_COUNT = "VALID_ORDERS_COUNT";

    public static final String ORDER_ID = "ORDER_ID";

    public static final String ORDER_CREATE_TIME = "ORDER_CREATE_TIME";

    public static Long getValidOrdersCount(Message<?> message) {
        return requireHeader(message.getHeaders().get(VALID_ORDERS_COUNT, Long.class), VALID_ORDERS_COUNT);
    }

    public static Long getOrderId(Message<?> message) {
        return requireHeader(message.getHeaders().get(ORDER_ID, Long.class), ORDER_ID);
    }

    public static LocalDateTime getCreateTime(Message<?> message) {
        return requireHeader(message.getHeaders().get(ORDER_CREATE_TIME, LocalDateTime.class), ORDER_CREATE_TIME);
    }

    private static <T> T requireHeader(T value, String headerName) {
        if (value == null) {
            throw new MissingHeaderException(headerName);
        }

        return value;
    }
}
