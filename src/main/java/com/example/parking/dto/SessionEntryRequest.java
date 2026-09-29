package com.example.parking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SessionEntryRequest(
        @Schema(example = "1")
        @NotNull(message = "vehicleId обязателен")
        Long vehicleId,

        @Schema(example = "1")
        @NotNull(message = "spotId обязателен")
        Long spotId
) {
}
