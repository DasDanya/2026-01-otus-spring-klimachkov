package ru.klimachkov.hw.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.klimachkov.hw.domain.CompleteOrder;
import ru.klimachkov.hw.domain.Device;
import ru.klimachkov.hw.domain.DeviceType;
import ru.klimachkov.hw.domain.Order;
import ru.klimachkov.hw.domain.OrderItem;
import ru.klimachkov.hw.domain.Priority;
import ru.klimachkov.hw.domain.RepairedItem;
import ru.klimachkov.hw.gateway.ServiceCenterGateway;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование выполнения заказов")
@SpringBootTest
@ActiveProfiles("test")
public class ServiceCenterFlowTest {

    @Autowired
    private ServiceCenterGateway serviceCenterGateway;

    @DisplayName("Должен выполнить валидные заказы (имеющие элементы)")
    @Test
    void shouldCompleteValidOrders() {
        Order firstCompletedOrder = new Order(1L, LocalDateTime.now(),
                Collections.singletonList(new OrderItem(new Device("TV", DeviceType.TV), Priority.HIGH))
        );

        Order secondCompletedOrder = new Order(2L, LocalDateTime.now(), List.of(
                new OrderItem(new Device("SMARTPHONE", DeviceType.SMARTPHONE), Priority.MEDIUM),
                new OrderItem(new Device("LAPTOP", DeviceType.LAPTOP), Priority.NORMAL))
        );

        Order invalidOrder = new Order(3L, LocalDateTime.now(), Collections.emptyList());

        Collection<CompleteOrder> completedOrders = serviceCenterGateway.complete(
                List.of(secondCompletedOrder, firstCompletedOrder, invalidOrder));

        assertThat(completedOrders)
                .isNotNull()
                .hasSize(2);

        List<CompleteOrder> completedOrdersList = new ArrayList<>(completedOrders);

        assertCompleteOrder(firstCompletedOrder, completedOrdersList.get(0),
                Collections.singletonList(BigDecimal.valueOf(15000.0)),  BigDecimal.valueOf(15000.0));

        assertCompleteOrder(secondCompletedOrder, completedOrdersList.get(1),
                List.of(BigDecimal.valueOf(3750.0), new BigDecimal(5000)), BigDecimal.valueOf(8750.0));
    }

    @DisplayName("Должен вернуть null коллекцию выполненных заказов, если исходные заказы невалидные " +
            "(не содержат элементы)")
    @Test
    void shouldReturnNullCompletedOrdersCollectionIfAllOrdersIsInvalid() {
        List<Order> invalidOrders = IntStream.range(1, 3)
                .mapToObj(id -> new Order(id, LocalDateTime.now(), Collections.emptyList()))
                .toList();

        Collection<CompleteOrder> completedOrders = serviceCenterGateway.complete(invalidOrders);
        assertThat(completedOrders).isNull();
    }

    private void assertCompleteOrder(
            Order sourceOrder,
            CompleteOrder completeOrder,
            List<BigDecimal> expectedRepairedItemsCost,
            BigDecimal expectedCompleteOrderCost
    ) {
        assertThat(completeOrder).isNotNull();
        assertThat(completeOrder.getId()).isEqualTo(sourceOrder.getId());
        assertThat(completeOrder.getCreateTime()).isEqualTo(sourceOrder.getCreateTime());

        assertThat(completeOrder.getItems().size()).isEqualTo(sourceOrder.getItems().size());

        for (int i = 0; i < completeOrder.getItems().size(); i++) {
            RepairedItem repairedItem = (RepairedItem) completeOrder.getItems().get(i);
            OrderItem orderItem = sourceOrder.getItems().get(i);

            assertThat(repairedItem.getDevice()).isEqualTo(orderItem.getDevice());
            assertThat(repairedItem.getPriority()).isEqualTo(orderItem.getPriority());
            assertThat(repairedItem.getRepairCost()).isEqualTo(expectedRepairedItemsCost.get(i));
        }

        assertThat(completeOrder.calculateTotalCost()).isEqualTo(expectedCompleteOrderCost);
    }
}
