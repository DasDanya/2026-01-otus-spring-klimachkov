package ru.klimachkov.hw.services;

import ru.klimachkov.hw.domain.OrderItem;
import ru.klimachkov.hw.domain.RepairedItem;

public interface RepairService {

    RepairedItem repair(OrderItem item);
}
