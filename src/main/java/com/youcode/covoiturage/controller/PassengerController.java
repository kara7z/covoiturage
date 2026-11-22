package com.youcode.covoiturage.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.youcode.covoiturage.dto.PassangerRequestDTO;
import com.youcode.covoiturage.dto.PassangerResponseDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.service.PassengerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

  private final PassengerService passengerService;

  @PostMapping
  public ResponseEntity<PassangerResponseDTO> create(@Valid @RequestBody PassangerRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(passengerService.create(requestDTO));
  }

  @GetMapping("/{id}")
  public PassangerResponseDTO findById(@PathVariable Long id) {
    return passengerService.findById(id);
  }

  @GetMapping
  public List<PassangerResponseDTO> findAll() {
    return passengerService.findAll();
  }

  @GetMapping("/search")
  public PassangerResponseDTO findByEmail(@RequestParam("email") String email) {
    return passengerService.findByEmail(email);
  }

  @GetMapping("/{id}/reservations")
  public Page<ReservationResponseDTO> findReservationHistory(
      @PathVariable Long id,
      @RequestParam Long requestingPassengerId,
      @PageableDefault(size = 20, sort = "reservationDate", direction = Sort.Direction.DESC) Pageable pageable) {
    return passengerService.findReservationHistory(id, requestingPassengerId, pageable);
  }

  @PutMapping("/{id}")
  public PassangerResponseDTO update(@PathVariable Long id,
      @RequestParam Long passengerId,
      @Valid @RequestBody PassangerRequestDTO requestDTO) {
    return passengerService.update(id, passengerId, requestDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, @RequestParam Long passengerId) {
    passengerService.delete(id, passengerId);
  }
}
