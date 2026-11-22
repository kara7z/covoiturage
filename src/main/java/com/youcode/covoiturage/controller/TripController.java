package com.youcode.covoiturage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.service.TripService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;

  @PostMapping
  public ResponseEntity<TripResponseDTO> create(@Valid @RequestBody TripRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(tripService.create(requestDTO));
  }
}
