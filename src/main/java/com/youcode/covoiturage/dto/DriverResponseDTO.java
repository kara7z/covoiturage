package com.youcode.covoiturage.dto;

/**
 * DriverResponseDTO
 */
public record DriverResponseDTO(
    Long id,
    String fullName,
    String email,
    String phone,
    String licenseNumber) {
}
