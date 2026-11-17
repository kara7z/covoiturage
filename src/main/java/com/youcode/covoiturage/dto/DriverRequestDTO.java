package com.youcode.covoiturage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload used to create or update a driver.
 */
public record DriverRequestDTO(
    @NotBlank(message = "First name is required")
    @Size(max = 255, message = "First name must not exceed 255 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 255, message = "Last name must not exceed 255 characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone,

    @NotBlank(message = "License number is required")
    @Size(max = 255, message = "License number must not exceed 255 characters")
    String licenseNumber
) {
}
