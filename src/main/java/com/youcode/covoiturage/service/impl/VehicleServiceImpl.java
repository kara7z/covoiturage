package com.youcode.covoiturage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.VehicleRequestDTO;
import com.youcode.covoiturage.dto.VehicleResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.VehicleMapper;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.repository.VehicleRepository;
import com.youcode.covoiturage.service.VehicleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VehicleServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

  private final VehicleRepository vehicleRepository;
  private final DriverRepository driverRepository;
  private final VehicleMapper vehicleMapper;

  @Override
  @Transactional
  public VehicleResponseDTO create(VehicleRequestDTO requestDTO) {
    log.info("Creating vehicle with license plate {} for driver id {}", requestDTO.licensePlate(),
        requestDTO.driverId());
    Driver driver = findDriver(requestDTO.driverId());
    Vehicle vehicle = vehicleMapper.toEntity(requestDTO, driver);
    VehicleResponseDTO response = vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    log.info("Vehicle created with id {}", response.id());
    return response;
  }

  @Override
  public VehicleResponseDTO findById(Long id) {
    log.debug("Fetching vehicle with id {}", id);
    return vehicleMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<VehicleResponseDTO> findAll() {
    log.debug("Fetching all vehicles");
    return vehicleRepository.findAll().stream()
        .map(vehicleMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public VehicleResponseDTO update(Long id, VehicleRequestDTO requestDTO) {
    log.info("Updating vehicle with id {}", id);
    Vehicle vehicle = findEntity(id);
    Driver driver = findDriver(requestDTO.driverId());
    vehicleMapper.updateEntity(vehicle, requestDTO, driver);
    VehicleResponseDTO response = vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    log.info("Vehicle updated with id {}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.info("Deleting vehicle with id {}", id);
    vehicleRepository.delete(findEntity(id));
    log.info("Vehicle deleted with id {}", id);
  }

  private Vehicle findEntity(Long id) {
    return vehicleRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Vehicle not found with id {}", id);
          return new ResourceNotFoundException("Vehicle not found with id " + id);
        });
  }

  private Driver findDriver(Long id) {
    return driverRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Driver not found with id {}", id);
          return new ResourceNotFoundException("Driver not found with id " + id);
        });
  }
}
