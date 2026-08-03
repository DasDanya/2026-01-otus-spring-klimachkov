package ru.klimachkov.hw.integration;

import org.springframework.integration.channel.PriorityChannel;
import org.springframework.messaging.Message;
import ru.klimachkov.hw.domain.OrderItem;

import java.util.Comparator;

/**
 * Кастомный {@link PriorityChannel} для элементов заказа
 */
public class OrderItemPriorityChannel extends PriorityChannel {

    public OrderItemPriorityChannel(int capacity) {
        super(capacity, new OrderItemPriorityComparator());
        setDatatypes(OrderItem.class);
    }

    /**
     * Компаратор сообщений с {@link OrderItem}, определяющий порядок обработки
     * <p>
     * Сортировка выполняется по следующим правилам:
     * <ul>
     *     <li>Сначала учитывается приоритет {@link OrderItem} (меньшее значение — выше приоритет);</li>
     *     <li>При равном приоритете сообщения упорядочиваются по времени создания заказа
     *     (элемент более раннего заказа обрабатывается первым)</li>
     * </ul>
     */
    private static class OrderItemPriorityComparator implements Comparator<Message<?>> {

        @Override
        public int compare(Message<?> firstMessage, Message<?> secondMessage) {
            if (firstMessage.getPayload() instanceof OrderItem firstOrderItem
                    && secondMessage.getPayload() instanceof OrderItem secondOrderItem
            ) {
                int compareResult = firstOrderItem.getPriority().compareTo(secondOrderItem.getPriority());

                if (compareResult == 0) {
                    compareResult = OrderHeaders.getCreateTime(firstMessage).compareTo(
                            OrderHeaders.getCreateTime(secondMessage));
                }

                return compareResult;
            }

            return 0;
        }
    }
}
