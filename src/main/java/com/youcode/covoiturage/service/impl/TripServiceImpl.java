package com.youcode.covoiturage.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.dto.TripStopRequestDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.StopPoint;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.enums.TripStatus;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.exception.ForbiddenException;
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
    ensureVehicleOwnership(driver, vehicle);
    List<TripStopRequestDTO> stops = validateStops(requestDTO.stopPoints());
    ensureSeatsFitVehicle(requestDTO.offeredSeats(), vehicle);

    Trip trip = tripMapper.toEntity(requestDTO, driver, vehicle);
    applyRoute(trip, stops);
    trip.setStatus(TripStatus.PUBLISHED);
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
    ensureVehicleOwnership(driver, vehicle);
    List<TripStopRequestDTO> stops = validateStops(requestDTO.stopPoints());
    ensureSeatsFitVehicle(requestDTO.offeredSeats(), vehicle);

    tripMapper.updateEntity(trip, requestDTO, driver, vehicle);
    trip.getStopPoints().clear();
    applyRoute(trip, stops);
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

  private void applyRoute(Trip trip, List<TripStopRequestDTO> stops) {
    TripStopRequestDTO departure = stops.get(0);
    TripStopRequestDTO arrival = stops.get(stops.size() - 1);
    trip.setDepartureCity(departure.city());
    trip.setArrivalCity(arrival.city());
    trip.setDepartureDate(departure.passingTime());
    for (int order = 0; order < stops.size(); order++) {
      TripStopRequestDTO stop = stops.get(order);
      trip.addStopPoint(StopPoint.builder()
          .city(stop.city())
          .stopOrder(order)
          .passingTime(stop.passingTime())
          .priceFromDeparture(stop.priceFromDeparture())
          .build());
    }
  }

  private List<TripStopRequestDTO> validateStops(List<TripStopRequestDTO> stops) {
    if (stops == null || stops.size() < 2) {
      throw new BadRequestException("INVALID_TRIP_STOPS", "A trip must have at least two stops");
    }

    List<TripStopRequestDTO> normalized = stops.stream()
        .map(this::normalize)
        .toList();

    Set<String> cities = new HashSet<>();
    for (int index = 0; index < normalized.size(); index++) {
      TripStopRequestDTO current = normalized.get(index);
      if (!cities.add(current.city().toLowerCase(Locale.ROOT))) {
        throw new BusinessRuleException("DUPLICATE_STOP_CITY",
            "City " + current.city() + " appears more than once in the trip");
      }
      if (current.passingTime() == null || current.priceFromDeparture() == null) {
        throw new BadRequestException("INVALID_TRIP_STOPS",
            "Each stop must define a passing time and a price from departure");
      }
      if (index == 0) {
        if (!current.passingTime().isAfter(LocalDateTime.now())) {
          throw new BadRequestException("INVALID_DEPARTURE_DATE", "Departure must be in the future");
        }
        if (current.priceFromDeparture().compareTo(BigDecimal.ZERO) != 0) {
          throw new BadRequestException("INVALID_STOP_PRICE",
              "Price from departure must be 0 at the first stop");
        }
      } else {
        TripStopRequestDTO previous = normalized.get(index - 1);
        if (!current.passingTime().isAfter(previous.passingTime())) {
          throw new BadRequestException("INVALID_STOP_TIME",
              "Stop passing times must be strictly increasing");
        }
        if (current.priceFromDeparture().compareTo(previous.priceFromDeparture()) <= 0) {
          throw new BadRequestException("INVALID_STOP_PRICE",
              "Cumulative prices must be strictly increasing");
        }
      }
    }
    return normalized;
  }

  private TripStopRequestDTO normalize(TripStopRequestDTO stop) {
    if (stop == null || stop.city() == null || stop.city().isBlank()) {
      throw new BadRequestException("INVALID_TRIP_STOPS", "Each stop must define a city");
    }
    return new TripStopRequestDTO(stop.city().trim(), stop.passingTime(), stop.priceFromDeparture());
  }

  private void ensureVehicleOwnership(Driver driver, Vehicle vehicle) {
    if (vehicle.getDriver() == null
        || !Objects.equals(vehicle.getDriver().getId(), driver.getId())) {
      throw new ForbiddenException("VEHICLE_NOT_OWNED",
          "Vehicle does not belong to driver " + driver.getId());
    }
  }

  private void ensureSeatsFitVehicle(Integer offeredSeats, Vehicle vehicle) {
    int capacity = vehicle.getPassengerCapacity();
    if (offeredSeats == null || offeredSeats < 1 || offeredSeats > capacity) {
      throw new BadRequestException("INVALID_OFFERED_SEATS",
          "Offered seats must be between 1 and " + capacity);
    }
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
