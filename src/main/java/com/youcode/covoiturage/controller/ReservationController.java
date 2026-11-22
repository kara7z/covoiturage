package com.youcode.covoiturage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  public ResponseEntity<ReservationResponseDTO> create(
      @Valid @RequestBody ReservationRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(requestDTO));
  }
}