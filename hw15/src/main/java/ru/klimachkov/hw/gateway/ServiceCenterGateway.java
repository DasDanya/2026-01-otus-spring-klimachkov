package ru.klimachkov.hw.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.klimachkov.hw.domain.CompleteOrder;
import ru.klimachkov.hw.domain.Order;

import java.util.Collection;

@MessagingGateway
public interface ServiceCenterGateway {

    @Gateway(requestChannel = "ordersInputChannel", replyChannel = "completedOrdersChannel", replyTimeout = 60000)
    Collection<CompleteOrder> complete(Collection<Order> orders);
}
