package com.example.parking.dto;

import com.example.parking.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParkingSpotRequest(
        @Schema(example = "A-01")
        @NotBlank(message = "Номер места обязателен")
        @Size(max = 20, message = "Номер места не длиннее 20 символов")
        String number,

        @Schema(example = "CAR")
        @NotNull(message = "Тип места обязателен (CAR, MOTORCYCLE, TRUCK)")
        VehicleType type
) {
}
