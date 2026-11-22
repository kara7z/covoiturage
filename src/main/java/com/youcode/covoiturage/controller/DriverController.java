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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.youcode.covoiturage.dto.DriverRequestDTO;
import com.youcode.covoiturage.dto.DriverResponseDTO;
import com.youcode.covoiturage.service.DriverService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

  private final DriverService driverService;

  @PostMapping
  public ResponseEntity<DriverResponseDTO> create(@Valid @RequestBody DriverRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(requestDTO));
  }

  @GetMapping("/{id}")
  public DriverResponseDTO findById(@PathVariable Long id) {
    return driverService.findById(id);
  }

  @GetMapping
  public List<DriverResponseDTO> findAll() {
    return driverService.findAll();
  }

  @GetMapping("/search")
  public List<DriverResponseDTO> search(@RequestParam("query") String query) {
    return driverService.search(query);
  }

  @PutMapping("/{id}")
  public DriverResponseDTO update(@PathVariable Long id,
      @RequestParam Long driverId,
      @Valid @RequestBody DriverRequestDTO requestDTO) {
    return driverService.update(id, driverId, requestDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, @RequestParam Long driverId) {
    driverService.delete(id, driverId);
  }
}
