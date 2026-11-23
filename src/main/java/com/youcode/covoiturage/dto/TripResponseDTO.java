package com.youcode.covoiturage.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.youcode.covoiturage.enums.TripStatus;

/**
 * TripResponseDTO
 */
public record TripResponseDTO(
    Long id,
    Long driverId,
    Long vehicleId,
    String departureCity,
    String arrivalCity,
    LocalDateTime departureDate,
    Integer offeredSeats,
    TripStatus status,
    List<StopPointResponseDTO> stopPoints) {
}
