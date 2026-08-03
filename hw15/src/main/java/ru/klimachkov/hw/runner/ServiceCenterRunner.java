package ru.klimachkov.hw.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.klimachkov.hw.domain.CompleteOrder;
import ru.klimachkov.hw.domain.Device;
import ru.klimachkov.hw.domain.DeviceType;
import ru.klimachkov.hw.domain.Order;
import ru.klimachkov.hw.domain.OrderItem;
import ru.klimachkov.hw.domain.Priority;
import ru.klimachkov.hw.gateway.ServiceCenterGateway;
import ru.klimachkov.hw.services.CompleteOrderPrintService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class ServiceCenterRunner implements CommandLineRunner {

    private final ServiceCenterGateway serviceCenterGateway;

    private final CompleteOrderPrintService completeOrderPrintService;

    @Override
    public void run(String... args) {
        Collection<CompleteOrder> completedOrders = serviceCenterGateway.complete(createOrders());

        if (completedOrders != null) {
            completedOrders.forEach(completeOrderPrintService::print);
        } else {
            log.warn("Completed orders collection is null");
        }
    }

    private Collection<Order> createOrders() {
        // Выполнится вторым
        Order firstOrder = new Order(1L, LocalDateTime.now(),
                List.of(new OrderItem(new Device("LAPTOP1", DeviceType.LAPTOP), Priority.NORMAL)));

        // Выполнится первым
        Order secondOrder = new Order(2L, LocalDateTime.now(),
                List.of(new OrderItem(new Device("SMARTPHONE2", DeviceType.SMARTPHONE), Priority.NORMAL),
                        new OrderItem(new Device("TV2", DeviceType.TV), Priority.HIGH)));

        Order thirdOrder = new Order(3L, LocalDateTime.now(), Collections.emptyList()); // Будет пропущен

        // Выполнится последним
        Order fourthOrder = new Order(4L, LocalDateTime.now(),
                List.of(new OrderItem(new Device("LAPTOP4", DeviceType.LAPTOP), Priority.MEDIUM),
                        new OrderItem(new Device("TV4", DeviceType.TV), Priority.MEDIUM)));

        // Выполнится перед четвертым, т.к 1) приоритет ремонта LAPTOP выше 2) Приоритеты ремонта TV совпадают, но
        // у данного заказа дата создания раньше
        Order fifthOrder = new Order(5L, LocalDateTime.now().minusMinutes(1),
                List.of(new OrderItem(new Device("LAPTOP5", DeviceType.LAPTOP), Priority.HIGH),
                        new OrderItem(new Device("TV5", DeviceType.TV), Priority.MEDIUM)));

        return List.of(firstOrder, secondOrder, thirdOrder, fourthOrder, fifthOrder);
    }
}
