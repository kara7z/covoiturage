package com.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * TripStopRequestDTO
 */
public record TripStopRequestDTO(
    @NotBlank(message = "City is required") @Size(max = 100, message = "City must not exceed 100 characters") String city,

    @NotNull(message = "Passing time is required") LocalDateTime passingTime,

    @NotNull(message = "Price from departure is required") @DecimalMin(value = "0.0", message = "Price from departure must not be negative") BigDecimal priceFromDeparture) {
}
