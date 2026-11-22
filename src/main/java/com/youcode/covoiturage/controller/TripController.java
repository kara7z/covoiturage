package com.youcode.covoiturage.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.youcode.covoiturage.dto.TripDetailResponseDTO;
import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.dto.TripSearchResponseDTO;
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

  @GetMapping("/search")
  public Page<TripSearchResponseDTO> search(
      @RequestParam String departureCity,
      @RequestParam String arrivalCity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(required = false) BigDecimal maxPrice,
      @PageableDefault(size = 10) Pageable pageable) {
    return tripService.search(departureCity, arrivalCity, date, maxPrice, pageable);
  }

  @GetMapping("/{id}")
  public TripDetailResponseDTO findDetail(@PathVariable Long id) {
    return tripService.findDetail(id);
  }
}
