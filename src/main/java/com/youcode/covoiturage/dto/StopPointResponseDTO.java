package com.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * StopPointResponseDTO
 */
public record StopPointResponseDTO(
    Long id,
    Long tripId,
    String city,
    Integer stopOrder,
    LocalDateTime passingTime,
    BigDecimal priceFromDeparture) {
}
