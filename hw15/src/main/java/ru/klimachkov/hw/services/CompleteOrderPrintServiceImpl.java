package ru.klimachkov.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.klimachkov.hw.domain.CompleteOrder;

import java.util.stream.Collectors;

/**
 * Сервис вывода информации о выполненном заказе.
 * Формирует и выводит информацию о заказе, включая элементы и общую стоимость
 */
@Service
@Slf4j
public class CompleteOrderPrintServiceImpl implements CompleteOrderPrintService {

    @Override
    public void print(CompleteOrder completeOrder) {
        String itemsInfo = completeOrder.getItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\t"));

        log.info(
            """
                \nCompleted order:
                \tid={}
                \tcreatedAt={}
                \tcompletedAt={}
                \titems=[{}]
                \ttotalCost={}
            """,
                completeOrder.getId(),
                completeOrder.getCreateTime(),
                completeOrder.getCompleteTime(),
                itemsInfo,
                completeOrder.calculateTotalCost()
        );
    }
}
