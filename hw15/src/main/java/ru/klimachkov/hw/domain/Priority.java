package ru.klimachkov.hw.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum Priority {
    HIGH(BigDecimal.valueOf(2.0), 0.5),
    MEDIUM(BigDecimal.valueOf(1.5), 0.75),
    NORMAL(BigDecimal.valueOf(1), 1.0);

    private final BigDecimal costMultiplier;

    private final double timeFactor;
}
