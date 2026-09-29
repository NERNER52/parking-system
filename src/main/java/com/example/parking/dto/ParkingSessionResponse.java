package com.example.parking.dto;

import com.example.parking.entity.ParkingSession;
import com.example.parking.enums.SessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParkingSessionResponse(
        Long id,
        Long vehicleId,
        String licensePlate,
        Long spotId,
        String spotNumber,
        LocalDateTime entryTime,
        LocalDateTime exitTime,
        BigDecimal cost,
        SessionStatus status
) {
    public static ParkingSessionResponse from(ParkingSession s) {
        return new ParkingSessionResponse(
                s.getId(),
                s.getVehicle().getId(),
                s.getVehicle().getLicensePlate(),
                s.getSpot().getId(),
                s.getSpot().getNumber(),
                s.getEntryTime(),
                s.getExitTime(),
                s.getCost(),
                s.getStatus()
        );
    }
}
