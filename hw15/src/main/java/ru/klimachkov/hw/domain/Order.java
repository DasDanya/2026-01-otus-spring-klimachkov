package ru.klimachkov.hw.domain;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {

    private final long id;

    private final LocalDateTime createTime;

    @ToString.Exclude
    private final List<? extends OrderItem> items;
}
