package com.youcode.covoiturage.exception;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String TYPE_PREFIX = "https://api.covoiturage.local/problems/";

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ProblemDetail> handleBusiness(BusinessException exception, HttpServletRequest request) {
    log.warn("Business error [{}]: {}", exception.getErrorCode(), exception.getMessage());
    return ResponseEntity.status(exception.getHttpStatus())
        .body(buildProblem(exception.getHttpStatus(), exception.getErrorCode(), exception.getMessage(), request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception,
      HttpServletRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    for (FieldError error : exception.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    log.warn("Validation failed: {}", errors);
    ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", request);
    problem.setProperty("errors", errors);
    return ResponseEntity.badRequest().body(problem);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraint(ConstraintViolationException exception,
      HttpServletRequest request) {
    log.warn("Constraint violation: {}", exception.getMessage());
    return ResponseEntity.badRequest()
        .body(buildProblem(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", exception.getMessage(), request));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ProblemDetail> handleNoHandler(NoHandlerFoundException exception,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(buildProblem(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "Endpoint not found", request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGeneric(Exception exception, HttpServletRequest request) {
    log.error("Unexpected error", exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Something went wrong", request));
  }

  private ProblemDetail buildProblem(HttpStatus status, String errorCode, String message, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, message);
    problem.setType(URI.create(TYPE_PREFIX + errorCode));
    problem.setTitle(errorCode);
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("timestamp", Instant.now().toString());
    return problem;
  }
}
