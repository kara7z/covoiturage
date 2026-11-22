package com.youcode.covoiturage.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * TripRequestDTO
 */
public record TripRequestDTO(
    @NotNull(message = "Driver id is required") Long driverId,

    @NotNull(message = "Vehicle id is required") Long vehicleId,

    @NotNull(message = "Offered seats is required") @Min(value = 1, message = "Offered seats must be at least 1") Integer offeredSeats,

    @NotNull(message = "Stop points are required") @Size(min = 2, message = "A trip must have at least two stops") @Valid List<TripStopRequestDTO> stopPoints) {
}
