package com.example.parking.dto;

import com.example.parking.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleRequest(
        @Schema(example = "01KG123ABC")
        @NotBlank(message = "Госномер обязателен")
        @Size(max = 20, message = "Госномер не длиннее 20 символов")
        String licensePlate,

        @Schema(example = "CAR")
        @NotNull(message = "Тип автомобиля обязателен (CAR, MOTORCYCLE, TRUCK)")
        VehicleType type,

        @Schema(example = "Алексей")
        @Size(max = 100, message = "Имя владельца не длиннее 100 символов")
        String ownerName
) {
}
