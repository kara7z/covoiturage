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

/**
 * VehicleServiceImpl
 */
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
    Driver driver = findDriver(requestDTO.driverId());
    Vehicle vehicle = vehicleMapper.toEntity(requestDTO, driver);
    return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
  }

  @Override
  public VehicleResponseDTO findById(Long id) {
    return vehicleMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<VehicleResponseDTO> findAll() {
    return vehicleRepository.findAll().stream()
        .map(vehicleMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public VehicleResponseDTO update(Long id, VehicleRequestDTO requestDTO) {
    Vehicle vehicle = findEntity(id);
    Driver driver = findDriver(requestDTO.driverId());
    vehicleMapper.updateEntity(vehicle, requestDTO, driver);
    return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    vehicleRepository.delete(findEntity(id));
  }

  private Vehicle findEntity(Long id) {
    return vehicleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));
  }

  private Driver findDriver(Long id) {
    return driverRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id " + id));
  }
}
