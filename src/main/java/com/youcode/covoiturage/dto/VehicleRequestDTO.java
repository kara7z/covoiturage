package com.youcode.covoiturage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * VehicleRequestDTO
 */
public record VehicleRequestDTO(
    @NotBlank(message = "Brand is required") @Size(max = 100, message = "Brand must not exceed 100 characters") String brand,

    @NotBlank(message = "Model is required") @Size(max = 100, message = "Model must not exceed 100 characters") String model,

    @NotBlank(message = "License plate is required") @Size(max = 50, message = "License plate must not exceed 50 characters") String licensePlate,

    @NotBlank(message = "Color is required") @Size(max = 50, message = "Color must not exceed 50 characters") String color,

    @NotNull(message = "Passenger capacity is required") @Min(value = 1, message = "Passenger capacity must be at least 1") @Max(value = 8, message = "Passenger capacity must not exceed 8") Integer passengerCapacity,

    @NotNull(message = "Driver id is required") Long driverId) {
}
