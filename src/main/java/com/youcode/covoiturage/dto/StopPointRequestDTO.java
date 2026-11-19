package com.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * StopPointRequestDTO
 */
public record StopPointRequestDTO(
    @NotNull(message = "Trip id is required") Long tripId,

    @NotBlank(message = "City is required") @Size(max = 100, message = "City must not exceed 100 characters") String city,

    @NotNull(message = "Stop order is required") @Min(value = 1, message = "Stop order must be at least 1") Integer stopOrder,

    @NotNull(message = "Passing time is required") LocalDateTime passingTime,

    @NotNull(message = "Price from departure is required") @DecimalMin(value = "0.0", message = "Price from departure must be positive") BigDecimal priceFromDeparture) {
}
