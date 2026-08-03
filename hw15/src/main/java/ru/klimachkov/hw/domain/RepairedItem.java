package ru.klimachkov.hw.domain;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString(callSuper = true)
public class RepairedItem extends OrderItem {

    private final BigDecimal repairCost;

    public RepairedItem(OrderItem orderItem, BigDecimal repairCost) {
        super(orderItem.getDevice(), orderItem.getPriority());
        this.repairCost = repairCost;
    }
}
