package ru.klimachkov.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.klimachkov.hw.domain.OrderItem;
import ru.klimachkov.hw.domain.RepairedItem;

import java.math.BigDecimal;

/**
 * Сервис ремонта элементов заказа
 */
@Service
@Slf4j
public class RepairServiceImpl implements RepairService {

    /**
     * Выполняет ремонт элемента заказа.
     * Имитирует процесс ремонта, рассчитывает стоимость и возвращает результат
     * @param item элемент заказа
     * @return отремонтированный элемент с рассчитанной стоимостью
     */
    @Override
    public RepairedItem repair(OrderItem item) {
        log.info("Starting repairing of the item: {}", item);

        BigDecimal repairCost = item.getDevice().deviceType().getCost().multiply(
                item.getPriority().getCostMultiplier());

        long repairTime = Math.round(
                item.getDevice().deviceType().getRepairTime() * item.getPriority().getTimeFactor());

        try {
            Thread.sleep(repairTime);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while repairing item: {}", item, ex);
            throw new RuntimeException("Repair interrupted", ex);
        }

        RepairedItem repairedItem = new RepairedItem(item, repairCost);

        log.info("Repair completed for item: {}", repairedItem);

        return repairedItem;
    }
}
