package com.youcode.covoiturage.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.youcode.covoiturage.enums.TripStatus;

/**
 * TripDetailResponseDTO
 */
public record TripDetailResponseDTO(
    Long id,
    Long driverId,
    String driverName,
    Long vehicleId,
    String vehicleBrand,
    String vehicleModel,
    String departureCity,
    String arrivalCity,
    LocalDateTime departureDate,
    Integer offeredSeats,
    Long remainingSeats,
    TripStatus status,
    List<StopPointResponseDTO> stopPoints) {
}
