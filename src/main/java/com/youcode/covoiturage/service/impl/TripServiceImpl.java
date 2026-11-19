package com.youcode.covoiturage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.TripMapper;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.repository.TripRepository;
import com.youcode.covoiturage.repository.VehicleRepository;
import com.youcode.covoiturage.service.TripService;

import lombok.RequiredArgsConstructor;

/**
 * TripServiceImpl
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripServiceImpl implements TripService {

  private final TripRepository tripRepository;
  private final DriverRepository driverRepository;
  private final VehicleRepository vehicleRepository;
  private final TripMapper tripMapper;

  @Override
  @Transactional
  public TripResponseDTO create(TripRequestDTO requestDTO) {
    Driver driver = findDriver(requestDTO.driverId());
    Vehicle vehicle = findVehicle(requestDTO.vehicleId());
    Trip trip = tripMapper.toEntity(requestDTO, driver, vehicle);
    return tripMapper.toResponseDTO(tripRepository.save(trip));
  }

  @Override
  public TripResponseDTO findById(Long id) {
    return tripMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<TripResponseDTO> findAll() {
    return tripRepository.findAll().stream()
        .map(tripMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public TripResponseDTO update(Long id, TripRequestDTO requestDTO) {
    Trip trip = findEntity(id);
    Driver driver = findDriver(requestDTO.driverId());
    Vehicle vehicle = findVehicle(requestDTO.vehicleId());
    tripMapper.updateEntity(trip, requestDTO, driver, vehicle);
    return tripMapper.toResponseDTO(tripRepository.save(trip));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    tripRepository.delete(findEntity(id));
  }

  private Trip findEntity(Long id) {
    return tripRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + id));
  }

  private Driver findDriver(Long id) {
    return driverRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id " + id));
  }

  private Vehicle findVehicle(Long id) {
    return vehicleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));
  }
}
