package com.example.parking.dto;

import com.example.parking.entity.ParkingSpot;
import com.example.parking.enums.VehicleType;

public record ParkingSpotResponse(
        Long id,
        String number,
        VehicleType type,
        boolean occupied
) {
    public static ParkingSpotResponse from(ParkingSpot s) {
        return new ParkingSpotResponse(s.getId(), s.getNumber(), s.getType(), s.isOccupied());
    }
}
