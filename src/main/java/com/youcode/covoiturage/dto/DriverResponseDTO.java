package com.youcode.covoiturage.dto;

/**
 * Driver representation returned by the API.
 */
public record DriverResponseDTO(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String licenseNumber
) {
}
