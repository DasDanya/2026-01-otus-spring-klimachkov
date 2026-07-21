package ru.klimachkov.hw.domain;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
public class CompleteOrder extends Order {

    private final LocalDateTime completeTime;

    public CompleteOrder(long id, LocalDateTime createTime, LocalDateTime completeTime, List<RepairedItem> items) {
        super(id, createTime, items);
        this.completeTime = completeTime;
    }

    public BigDecimal calculateTotalCost() {
        return getItems().stream()
                .map(item -> ((RepairedItem) item).getRepairCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
