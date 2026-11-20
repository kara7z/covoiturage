package com.youcode.covoiturage.dto;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    int status,
    String message,
    Map<String, String> errors,
    LocalDateTime timestamp) {

  public static ErrorResponse of(HttpStatus status, String message) {
    return new ErrorResponse(status.value(), message, Map.of(), LocalDateTime.now());
  }

  public static ErrorResponse of(HttpStatus status, String message, Map<String, String> errors) {
    return new ErrorResponse(status.value(), message, errors, LocalDateTime.now());
  }
}
