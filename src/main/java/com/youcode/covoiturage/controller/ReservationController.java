package com.youcode.covoiturage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

  @PatchMapping("/{id}/cancel")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(
      @PathVariable Long id,
      @RequestParam Long passengerId) {
    reservationService.cancel(id, passengerId);
  }

  @PutMapping("/{id}")
  public ReservationResponseDTO update(@PathVariable Long id,
      @RequestParam Long passengerId,
      @Valid @RequestBody ReservationRequestDTO requestDTO) {
    return reservationService.update(id, passengerId, requestDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, @RequestParam Long passengerId) {
    reservationService.delete(id, passengerId);
  }
}