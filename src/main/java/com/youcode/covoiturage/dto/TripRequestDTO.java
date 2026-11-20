package com.youcode.covoiturage.dto;

import java.time.LocalDateTime;

import com.youcode.covoiturage.enums.TripStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * TripRequestDTO
 */
public record TripRequestDTO(
    @NotNull(message = "Driver id is required") Long driverId,

    @NotNull(message = "Vehicle id is required") Long vehicleId,

    @NotBlank(message = "Departure city is required") @Size(max = 100, message = "Departure city must not exceed 100 characters") String departureCity,

    @NotBlank(message = "Arrival city is required") @Size(max = 100, message = "Arrival city must not exceed 100 characters") String arrivalCity,

    @NotNull(message = "Departure date is required") LocalDateTime departureDate,

    @NotNull(message = "Offered seats is required") @Min(value = 1, message = "Offered seats must be at least 1") Integer offeredSeats,

    @NotNull(message = "Status is required") TripStatus status) {
}
