package com.example.parking.enums;

import java.math.BigDecimal;

/**
 * Тип автомобиля (он же тип парковочного места) и его тариф в сомах за час.
 */
public enum VehicleType {
    CAR(new BigDecimal("100")),
    MOTORCYCLE(new BigDecimal("50")),
    TRUCK(new BigDecimal("150"));

    private final BigDecimal hourlyRate;

    VehicleType(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
}
