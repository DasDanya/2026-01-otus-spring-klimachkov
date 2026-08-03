package ru.klimachkov.hw.domain;

import lombok.Data;

@Data
public class OrderItem {

    private final Device device;

    private final Priority priority;
}
