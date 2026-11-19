package com.youcode.covoiturage.dto;

/**
 * PassangerResponseDTO
 */
public record PassangerResponseDTO(
    Long id,
    String fullName,
    String email,
    String phone) {
}
