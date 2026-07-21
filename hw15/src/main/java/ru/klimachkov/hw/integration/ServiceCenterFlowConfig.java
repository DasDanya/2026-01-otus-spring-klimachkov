package ru.klimachkov.hw.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.klimachkov.hw.domain.CompleteOrder;
import ru.klimachkov.hw.domain.DeviceType;
import ru.klimachkov.hw.domain.OrderItem;
import ru.klimachkov.hw.domain.Order;
import ru.klimachkov.hw.domain.RepairedItem;
import ru.klimachkov.hw.services.RepairService;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class ServiceCenterFlowConfig {

    private static final int QUEUE_CHANNEL_CAPACITY = 10;

    @Bean
    public MessageChannel ordersInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel invalidOrdersChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel smartphonesChannel() {
        return new OrderItemPriorityChannel(QUEUE_CHANNEL_CAPACITY);
    }

    @Bean
    public MessageChannel laptopsChannel() {
        return new OrderItemPriorityChannel(QUEUE_CHANNEL_CAPACITY);
    }

    @Bean
    public MessageChannel tvChannel() {
        return new OrderItemPriorityChannel(QUEUE_CHANNEL_CAPACITY);
    }

    @Bean
    public MessageChannel repairedItemsChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel completedOrdersChannel() {
        return new DirectChannel();
    }

    /**
     * Основной Flow заказов: фильтрация пустых заказов, разбиение на элементы,
     * маршрутизация в отдельные каналы по {@link DeviceType}
     * @return Flow заказов
     */
    @Bean
    @SuppressWarnings("unchecked")
    public IntegrationFlow ordersFlow() {
        return IntegrationFlow.from(ordersInputChannel())
                .enrichHeaders(headers -> headers.headerFunction(OrderHeaders.VALID_ORDERS_COUNT, message ->
                        ((List<Order>) message.getPayload()).stream()
                                .filter(order -> !order.getItems().isEmpty())
                                .count()
                ))
                .split()
                .filter(Order.class,
                        order -> !order.getItems().isEmpty(), filter -> filter.discardChannel(invalidOrdersChannel()))
                .enrichHeaders(headers ->
                        headers.headerFunction(OrderHeaders.ORDER_ID, message -> ((Order) message.getPayload()).getId())
                                .headerFunction(OrderHeaders.ORDER_CREATE_TIME,
                                        message -> ((Order) message.getPayload()).getCreateTime()))
                .split(Order.class, Order::getItems)
                .<OrderItem, String>route(orderItem -> switch (orderItem.getDevice().deviceType()) {
                    case SMARTPHONE -> "smartphonesChannel";
                    case LAPTOP -> "laptopsChannel";
                    case TV -> "tvChannel";
                })
                .get();
    }

    /**
     * Flow ремонта смартфонов: обрабатывает элементы заказа и отправляет отремонтированные в общий канал
     * @param repairService Сервис ремонта
     * @return Flow ремонта смартфонов
     */
    @Bean
    public IntegrationFlow smartphonesRepairFlow(RepairService repairService) {
        return createRepairDevicesFlow(repairService, smartphonesChannel());
    }

    /**
     * Flow ремонта ноутбуков: обрабатывает элементы заказа и отправляет отремонтированные в общий канал
     * @param repairService Сервис ремонта
     * @return Flow ремонта ноутбуков
     */
    @Bean
    public IntegrationFlow laptopsRepairFlow(RepairService repairService) {
        return createRepairDevicesFlow(repairService, laptopsChannel());
    }

    /**
     * Flow ремонта телевизоров: обрабатывает элементы заказа и отправляет отремонтированные в общий канал
     * @param repairService Сервис ремонта
     * @return Flow ремонта телевизоров
     */
    @Bean
    public IntegrationFlow tvRepairFlow(RepairService repairService) {
        return createRepairDevicesFlow(repairService, tvChannel());
    }

    /**
     * Flow завершения заказов: собирает элементы в заказы,
     * затем агрегирует их и отправляет результат в канал выполненных заказов
     */
    @Bean
    public IntegrationFlow completeOrdersFlow() {
        return IntegrationFlow.from(repairedItemsChannel())
                .aggregate(aggregator -> aggregator
                        .outputProcessor(messageGroup -> {
                            Message<?> oneMessage = messageGroup.getOne();

                            List<RepairedItem> repairedItems = messageGroup.getMessages().stream()
                                    .map(message -> (RepairedItem) message.getPayload())
                                    .toList();

                            return new CompleteOrder(
                                    OrderHeaders.getOrderId(oneMessage),
                                    OrderHeaders.getCreateTime(oneMessage),
                                    LocalDateTime.now(),
                                    repairedItems
                            );
                        })
                )
                .aggregate(aggregator -> aggregator.releaseStrategy(messageGroup ->
                        OrderHeaders.getValidOrdersCount(messageGroup.getOne()) == messageGroup.size()))
                .channel(completedOrdersChannel())
                .get();
    }

    /**
     * Flow невалидных заказов (заказов без элементов)
     * @return Flow невалидных заказов
     */
    @Bean
    public IntegrationFlow invalidOrdersFlow() {
        return IntegrationFlow.from(invalidOrdersChannel())
                .log(LoggingHandler.Level.WARN, ServiceCenterFlowConfig.class.getCanonicalName(),
                        message -> "Order without items is ignored: " + message.getPayload())
                .nullChannel();
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(DeviceType.values().length);
        return scheduler;
    }

    /**
     * Метод для создания Flow ремонта устройств: каждый элемент входного канала обрабатывается
     * сервисом ремонта, после чего отправляет отремонтированные элементы в соответствующий канал
     * @param repairService Сервис ремонта
     * @param fromChannel Канал с неотремонтированными элементами
     * @return Flow ремонта устройств
     */
    private IntegrationFlow createRepairDevicesFlow(RepairService repairService, MessageChannel fromChannel) {
        return IntegrationFlow.from(fromChannel)
                .handle(repairService, "repair")
                .channel(repairedItemsChannel())
                .get();
    }
}
