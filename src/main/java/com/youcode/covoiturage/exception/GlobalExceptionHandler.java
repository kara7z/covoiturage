package com.youcode.covoiturage.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.youcode.covoiturage.dto.ErrorResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
    log.warn("Business error: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.of(ex.getHttpStatus(), ex.getMessage());
    return ResponseEntity.status(ex.getHttpStatus()).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    log.warn("Validation failed: {}", errors);
    ErrorResponse error = ErrorResponse.of(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
    log.warn("Constraint violation: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex) {
    ErrorResponse error = ErrorResponse.of(HttpStatus.NOT_FOUND, "Endpoint not found");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    log.error("Unexpected error", ex);
    ErrorResponse error = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
