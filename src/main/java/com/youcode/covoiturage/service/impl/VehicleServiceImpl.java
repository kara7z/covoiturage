package com.youcode.covoiturage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.VehicleRequestDTO;
import com.youcode.covoiturage.dto.VehicleResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.exception.AlreadyExistsException;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.ForbiddenException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.VehicleMapper;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.repository.TripRepository;
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
  private final TripRepository tripRepository;
  private final DriverRepository driverRepository;
  private final VehicleMapper vehicleMapper;

  @Override
  @Transactional
  public VehicleResponseDTO create(Long driverId, VehicleRequestDTO requestDTO) {
    log.info("Creating vehicle with license plate {} for driver id {}", requestDTO.licensePlate(), driverId);
    ensurePassengerCapacity(requestDTO.passengerCapacity());
    ensureRequestOwner(driverId, requestDTO);
    ensureLicensePlateAvailable(requestDTO.licensePlate(), null);
    Driver driver = findDriver(driverId);
    Vehicle vehicle = vehicleMapper.toEntity(requestDTO, driver);
    VehicleResponseDTO response = vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    log.info("Vehicle created with id {}", response.id());
    return response;
  }

  @Override
  public VehicleResponseDTO findById(Long driverId, Long id) {
    log.debug("Fetching vehicle with id {}", id);
    return vehicleMapper.toResponseDTO(findOwnedEntity(driverId, id));
  }

  @Override
  public List<VehicleResponseDTO> findByDriverId(Long driverId) {
    log.debug("Fetching vehicles for driver {}", driverId);
    return vehicleRepository.findByDriverId(driverId).stream()
        .map(vehicleMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public VehicleResponseDTO update(Long driverId, Long id, VehicleRequestDTO requestDTO) {
    log.info("Updating vehicle with id {}", id);
    ensurePassengerCapacity(requestDTO.passengerCapacity());
    Vehicle vehicle = findOwnedEntity(driverId, id);
    ensureRequestOwner(driverId, requestDTO);
    ensureLicensePlateAvailable(requestDTO.licensePlate(), id);
    Driver driver = findDriver(driverId);
    vehicleMapper.updateEntity(vehicle, requestDTO, driver);
    VehicleResponseDTO response = vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    log.info("Vehicle updated with id {}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(Long driverId, Long id) {
    log.info("Deleting vehicle with id {}", id);
    Vehicle vehicle = findOwnedEntity(driverId, id);
    if (tripRepository.existsByVehicleId(id)) {
      throw new AlreadyExistsException("VEHICLE_IN_USE", "Vehicle linked to a trip cannot be deleted");
    }
    vehicleRepository.delete(vehicle);
    log.info("Vehicle deleted with id {}", id);
  }

  private Vehicle findEntity(Long id) {
    return vehicleRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Vehicle not found with id {}", id);
          return new ResourceNotFoundException("Vehicle not found with id " + id);
        });
  }

  private Vehicle findOwnedEntity(Long driverId, Long vehicleId) {
    Vehicle vehicle = findEntity(vehicleId);
    if (!java.util.Objects.equals(vehicle.getDriver().getId(), driverId)) {
      throw new ForbiddenException("Vehicle does not belong to driver " + driverId);
    }
    return vehicle;
  }

  private void ensureRequestOwner(Long driverId, VehicleRequestDTO requestDTO) {
    if (!driverId.equals(requestDTO.driverId())) {
      throw new ForbiddenException("Vehicle driver id must match the owning driver");
    }
  }

  private void ensurePassengerCapacity(Integer passengerCapacity) {
    if (passengerCapacity == null || passengerCapacity < 1 || passengerCapacity > 8) {
      throw new BadRequestException("INVALID_PASSENGER_CAPACITY",
          "Passenger capacity must be between 1 and 8 seats");
    }
  }

  private void ensureLicensePlateAvailable(String licensePlate, Long currentId) {
    boolean exists = currentId == null
        ? vehicleRepository.existsByLicensePlate(licensePlate)
        : vehicleRepository.existsByLicensePlateAndIdNot(licensePlate, currentId);
    if (exists) {
      throw new AlreadyExistsException("Vehicle with license plate " + licensePlate + " already exists");
    }
  }

  private Driver findDriver(Long id) {
    return driverRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Driver not found with id {}", id);
          return new ResourceNotFoundException("Driver not found with id " + id);
        });
  }
}
