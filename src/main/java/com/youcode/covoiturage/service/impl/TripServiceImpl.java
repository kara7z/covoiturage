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
import lombok.extern.slf4j.Slf4j;

/**
 * TripServiceImpl
 */
@Slf4j
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
    log.info("Creating trip for driver id {} with vehicle id {}", requestDTO.driverId(), requestDTO.vehicleId());
    Driver driver = findDriver(requestDTO.driverId());
    Vehicle vehicle = findVehicle(requestDTO.vehicleId());
    Trip trip = tripMapper.toEntity(requestDTO, driver, vehicle);
    TripResponseDTO response = tripMapper.toResponseDTO(tripRepository.save(trip));
    log.info("Trip created with id {}", response.id());
    return response;
  }

  @Override
  public TripResponseDTO findById(Long id) {
    log.debug("Fetching trip with id {}", id);
    return tripMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<TripResponseDTO> findAll() {
    log.debug("Fetching all trips");
    return tripRepository.findAll().stream()
        .map(tripMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public TripResponseDTO update(Long id, TripRequestDTO requestDTO) {
    log.info("Updating trip with id {}", id);
    Trip trip = findEntity(id);
    Driver driver = findDriver(requestDTO.driverId());
    Vehicle vehicle = findVehicle(requestDTO.vehicleId());
    tripMapper.updateEntity(trip, requestDTO, driver, vehicle);
    TripResponseDTO response = tripMapper.toResponseDTO(tripRepository.save(trip));
    log.info("Trip updated with id {}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.info("Deleting trip with id {}", id);
    tripRepository.delete(findEntity(id));
    log.info("Trip deleted with id {}", id);
  }

  private Trip findEntity(Long id) {
    return tripRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Trip not found with id {}", id);
          return new ResourceNotFoundException("Trip not found with id " + id);
        });
  }

  private Driver findDriver(Long id) {
    return driverRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Driver not found with id {}", id);
          return new ResourceNotFoundException("Driver not found with id " + id);
        });
  }

  private Vehicle findVehicle(Long id) {
    return vehicleRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Vehicle not found with id {}", id);
          return new ResourceNotFoundException("Vehicle not found with id " + id);
        });
  }
}
