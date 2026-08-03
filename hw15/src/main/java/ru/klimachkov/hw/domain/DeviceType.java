package ru.klimachkov.hw.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum DeviceType {
    SMARTPHONE(BigDecimal.valueOf(2500), 5000L),
    LAPTOP(BigDecimal.valueOf(5000), 7500L),
    TV(BigDecimal.valueOf(7500), 10000L);

    private final BigDecimal cost;

    private final long repairTime;
}
