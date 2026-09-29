package com.example.parking.dto;

import com.example.parking.entity.Vehicle;
import com.example.parking.enums.VehicleType;

public record VehicleResponse(
        Long id,
        String licensePlate,
        VehicleType type,
        String ownerName
) {
    public static VehicleResponse from(Vehicle v) {
        return new VehicleResponse(v.getId(), v.getLicensePlate(), v.getType(), v.getOwnerName());
    }
}
