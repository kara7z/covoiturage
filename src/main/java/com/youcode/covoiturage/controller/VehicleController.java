package com.youcode.covoiturage.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.youcode.covoiturage.dto.VehicleRequestDTO;
import com.youcode.covoiturage.dto.VehicleResponseDTO;
import com.youcode.covoiturage.service.VehicleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drivers/{driverId}/vehicles")
@RequiredArgsConstructor
public class VehicleController {

  private final VehicleService vehicleService;

  @PostMapping
  public ResponseEntity<VehicleResponseDTO> create(@PathVariable Long driverId,
      @Valid @RequestBody VehicleRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(driverId, requestDTO));
  }

  @GetMapping
  public List<VehicleResponseDTO> findByDriverId(@PathVariable Long driverId) {
    return vehicleService.findByDriverId(driverId);
  }

  @GetMapping("/{id}")
  public VehicleResponseDTO findById(@PathVariable Long driverId, @PathVariable Long id) {
    return vehicleService.findById(driverId, id);
  }

  @PutMapping("/{id}")
  public VehicleResponseDTO update(@PathVariable Long driverId, @PathVariable Long id,
      @Valid @RequestBody VehicleRequestDTO requestDTO) {
    return vehicleService.update(driverId, id, requestDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long driverId, @PathVariable Long id) {
    vehicleService.delete(driverId, id);
  }
}
