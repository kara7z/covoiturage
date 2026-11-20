package com.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.youcode.covoiturage.enums.ReservationStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ReservationRequestDTO
 */
public record ReservationRequestDTO(
    @NotNull(message = "Passenger id is required") Long passengerId,

    @NotNull(message = "Trip id is required") Long tripId,

    @NotBlank(message = "Boarding point is required") @Size(max = 100, message = "Boarding point must not exceed 100 characters") String boardingPoint,

    @NotBlank(message = "Drop off point is required") @Size(max = 100, message = "Drop off point must not exceed 100 characters") String dropOffPoint,

    @NotNull(message = "Number of seats is required") @Min(value = 1, message = "Number of seats must be at least 1") Integer numberOfSeats,

    @NotNull(message = "Total amount is required") @DecimalMin(value = "0.0", message = "Total amount must be positive") BigDecimal totalAmount,

    @NotNull(message = "Reservation date is required") LocalDateTime reservationDate,

    @NotNull(message = "Status is required") ReservationStatus status) {
}
