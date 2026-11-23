package com.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TripSearchResponseDTO
 */
public record TripSearchResponseDTO(
    Long id,
    Long driverId,
    String driverName,
    String departureCity,
    String arrivalCity,
    LocalDateTime departureTime,
    LocalDateTime arrivalTime,
    BigDecimal segmentPrice,
    Long remainingSeats) {
}
