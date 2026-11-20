package com.youcode.covoiturage.dto;

/**
 * VehicleResponseDTO
 */
public record VehicleResponseDTO(
    Long id,
    String brand,
    String model,
    String licensePlate,
    String color,
    Integer passengerCapacity,
    Long driverId) {
}
