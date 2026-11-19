package com.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.youcode.covoiturage.enums.ReservationStatus;

/**
 * ReservationResponseDTO
 */
public record ReservationResponseDTO(
    Long id,
    Long passengerId,
    Long tripId,
    String boardingPoint,
    String dropOffPoint,
    Integer numberOfSeats,
    BigDecimal totalAmount,
    LocalDateTime reservationDate,
    ReservationStatus status) {
}
