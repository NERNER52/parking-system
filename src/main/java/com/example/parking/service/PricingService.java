package com.example.parking.service;

import com.example.parking.enums.VehicleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Расчёт стоимости парковки.
 * Правило: почасовая оплата, каждый начатый час считается полностью, минимум 1 час.
 */
@Service
public class PricingService {

    private static final long SECONDS_IN_HOUR = 3600;

    public BigDecimal calculate(VehicleType type, LocalDateTime entryTime, LocalDateTime exitTime) {
        long seconds = Duration.between(entryTime, exitTime).getSeconds();
        long hours = Math.max(1, (seconds + SECONDS_IN_HOUR - 1) / SECONDS_IN_HOUR);
        return type.getHourlyRate().multiply(BigDecimal.valueOf(hours));
    }
}
