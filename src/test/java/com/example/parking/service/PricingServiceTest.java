package com.example.parking.service;

import com.example.parking.enums.VehicleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private final PricingService pricing = new PricingService();
    private final LocalDateTime start = LocalDateTime.of(2026, 9, 1, 10, 0, 0);

    @Test
    void shortStayIsChargedAsOneHour() {
        assertEquals(new BigDecimal("100"), pricing.calculate(VehicleType.CAR, start, start.plusMinutes(10)));
    }

    @Test
    void exactTwoHoursMotorcycle() {
        assertEquals(new BigDecimal("100"), pricing.calculate(VehicleType.MOTORCYCLE, start, start.plusHours(2)));
    }

    @Test
    void startedHourIsChargedFully() {
        assertEquals(new BigDecimal("200"), pricing.calculate(VehicleType.CAR, start, start.plusMinutes(61)));
    }

    @Test
    void truckThreeHours() {
        assertEquals(new BigDecimal("450"), pricing.calculate(VehicleType.TRUCK, start, start.plusHours(3)));
    }
}
