package com.youcode.covoiturage.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.youcode.covoiturage.dto.VehicleRequestDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.exception.AlreadyExistsException;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.ForbiddenException;
import com.youcode.covoiturage.mapper.VehicleMapper;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.repository.TripRepository;
import com.youcode.covoiturage.repository.VehicleRepository;

import jakarta.validation.Validation;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

  @Mock
  private VehicleRepository vehicleRepository;

  @Mock
  private DriverRepository driverRepository;

  @Mock
  private TripRepository tripRepository;

  @Mock
  private VehicleMapper vehicleMapper;

  @InjectMocks
  private VehicleServiceImpl vehicleService;

  @Test
  void create_rejectsDuplicateLicensePlate() {
    VehicleRequestDTO request = request(1L, 4);
    when(vehicleRepository.existsByLicensePlate("ABC-123")).thenReturn(true);

    assertThatThrownBy(() -> vehicleService.create(1L, request))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessage("Vehicle with license plate ABC-123 already exists");
    verify(vehicleRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void create_rejectsCapacityOutsidePassengerSeatRange() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      var validator = validatorFactory.getValidator();
      assertThat(validator.validate(request(1L, 9))).anySatisfy(violation -> {
          assertThat(violation.getPropertyPath().toString()).isEqualTo("passengerCapacity");
          assertThat(violation.getMessage()).isEqualTo("Passenger capacity must not exceed 8");
        });
      assertThat(validator.validate(request(1L, 0))).anySatisfy(violation -> {
          assertThat(violation.getPropertyPath().toString()).isEqualTo("passengerCapacity");
          assertThat(violation.getMessage()).isEqualTo("Passenger capacity must be at least 1");
        });
    }
  }

  @Test
  void create_rejectsInvalidCapacityAtServiceBoundary() {
    assertThatThrownBy(() -> vehicleService.create(1L, request(1L, 0)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Passenger capacity must be between 1 and 8 seats");
    assertThatThrownBy(() -> vehicleService.create(1L, request(1L, 9)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Passenger capacity must be between 1 and 8 seats");
  }

  @Test
  void findById_rejectsVehicleOwnedByAnotherDriver() {
    Driver owner = Driver.builder().firstName("Owner").lastName("Driver").email("owner@example.com")
        .phone("000").licenseNumber("LIC").build();
    Vehicle vehicle = Vehicle.builder().driver(owner).build();
    when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehicle));

    assertThatThrownBy(() -> vehicleService.findById(2L, 10L))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Vehicle does not belong to driver 2");
  }

  @Test
  void delete_rejectsVehicleLinkedToTrip() {
    Driver owner = Driver.builder().firstName("Owner").lastName("Driver").email("owner@example.com")
        .phone("000").licenseNumber("LIC").build();
    Vehicle vehicle = Vehicle.builder().driver(owner).build();
    when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehicle));
    when(tripRepository.existsByVehicleId(10L)).thenReturn(true);

    assertThatThrownBy(() -> vehicleService.delete(1L, 10L))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessage("Vehicle linked to a trip cannot be deleted");
    verify(vehicleRepository, never()).delete(vehicle);
  }

  private VehicleRequestDTO request(Long driverId, Integer passengerCapacity) {
    return new VehicleRequestDTO("Toyota", "Corolla", "ABC-123", "Blue", passengerCapacity, driverId);
  }
}
