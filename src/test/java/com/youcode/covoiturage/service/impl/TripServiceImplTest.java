package com.youcode.covoiturage.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
import com.youcode.covoiturage.mapper.TripMapper;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.repository.TripRepository;
import com.youcode.covoiturage.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

  private static final LocalDateTime DEPARTURE = LocalDateTime.now().plusDays(1).withNano(0);

  @Mock
  private TripRepository tripRepository;

  @Mock
  private DriverRepository driverRepository;

  @Mock
  private VehicleRepository vehicleRepository;

  @Mock
  private TripMapper tripMapper;

  @InjectMocks
  private TripServiceImpl tripService;

  @Test
  void create_publishesTripWithDerivedRouteAndOrderedStops() {
    Driver driver = driver(1L);
    Vehicle vehicle = vehicle(2L, driver, 4);
    givenOwnedVehicle(driver, vehicle);

    Trip trip = Trip.builder().build();
    TripResponseDTO expected = new TripResponseDTO(10L, 1L, 2L, "Rabat", "Marrakech",
        DEPARTURE, 3, TripStatus.PUBLISHED, List.of());
    when(tripMapper.toEntity(any(), eq(driver), eq(vehicle))).thenReturn(trip);
    when(tripRepository.save(trip)).thenReturn(trip);
    when(tripMapper.toResponseDTO(trip)).thenReturn(expected);

    TripResponseDTO response = tripService.create(request(3, validStops()));

    assertThat(response).isSameAs(expected);
    assertThat(trip.getDepartureCity()).isEqualTo("Rabat");
    assertThat(trip.getArrivalCity()).isEqualTo("Marrakech");
    assertThat(trip.getDepartureDate()).isEqualTo(DEPARTURE);
    assertThat(trip.getStatus()).isEqualTo(TripStatus.PUBLISHED);
    assertThat(trip.getStopPoints()).extracting(StopPoint::getCity)
        .containsExactly("Rabat", "Casablanca", "Settat", "Marrakech");
    assertThat(trip.getStopPoints()).extracting(StopPoint::getStopOrder)
        .containsExactly(0, 1, 2, 3);
  }

  @Test
  void create_rejectsTripWithFewerThanTwoStops() {
    Driver driver = driver(1L);
    Vehicle vehicle = vehicle(2L, driver, 4);
    givenOwnedVehicle(driver, vehicle);

    assertThatThrownBy(() -> tripService.create(request(3, List.of(stop("Rabat", DEPARTURE, "0")))))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("A trip must have at least two stops");
    verify(tripRepository, never()).save(any());
  }

  @Test
  void create_rejectsDepartureInThePast() {
    Driver driver = driver(1L);
    Vehicle vehicle = vehicle(2L, driver, 4);
    givenOwnedVehicle(driver, vehicle);
    List<TripStopRequestDTO> stops = List.of(
        stop("Rabat", LocalDateTime.now().minusHours(1), "0"),
        stop("Marrakech", LocalDateTime.now().plusHours(1), "120"));

    assertThatThrownBy(() -> tripService.create(request(3, stops)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Departure must be in the future");
    verify(tripRepository, never()).save(any());
  }

  @Test
  void create_rejectsNonIncreasingPassingTimes() {
    Driver driver = driver(1L);
    Vehicle vehicle = vehicle(2L, driver, 4);
    givenOwnedVehicle(driver, vehicle);
    List<TripStopRequestDTO> stops = List.of(
        stop("Rabat", DEPARTURE, "0"),
        stop("Marrakech", DEPARTURE, "120"));

    assertThatThrownBy(() -> tripService.create(request(3, stops)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Stop passing times must be strictly increasing");
    verify(tripRepository, never()).save(any());
  }

  @Test
  void create_rejectsCityAppearingTwice() {
    Driver driver = driver(1L);
    Vehicle vehicle = vehicle(2L, driver, 4);
    givenOwnedVehicle(driver, vehicle);
    List<TripStopRequestDTO> stops = List.of(
        stop("Rabat", DEPARTURE, "0"),
        stop("Casablanca", DEPARTURE.plusHours(1), "40"),
        stop("Rabat", DEPARTURE.plusHours(2), "70"));

    assertThatThrownBy(() -> tripService.create(request(3, stops)))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessage("City Rabat appears more than once in the trip");
    verify(tripRepository, never()).save(any());
  }

  @Test
  void create_rejectsNonZeroPriceAtFirstStop() {
    Driver driver = driver(1L);
    Vehicle vehicle = vehicle(2L, driver, 4);
    givenOwnedVehicle(driver, vehicle);
    List<TripStopRequestDTO> stops = List.of(
        stop("Rabat", DEPARTURE, "10"),
        stop("Marrakech", DEPARTURE.plusHours(4), "120"));

    assertThatThrownBy(() -> tripService.create(request(3, stops)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Price from departure must be 0 at the first stop");
    verify(tripRepository, never()).save(any());
  }

  @Test
  void create_rejectsNonIncreasingCumulativePrices() {
    Driver driver = driver(1L);
    Vehicle vehicle = vehicle(2L, driver, 4);
    givenOwnedVehicle(driver, vehicle);
    List<TripStopRequestDTO> stops = List.of(
        stop("Rabat", DEPARTURE, "0"),
        stop("Casablanca", DEPARTURE.plusHours(1), "40"),
        stop("Settat", DEPARTURE.plusHours(2), "40"));

    assertThatThrownBy(() -> tripService.create(request(3, stops)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Cumulative prices must be strictly increasing");
    verify(tripRepository, never()).save(any());
  }

  @Test
  void create_rejectsSeatsAboveVehicleCapacity() {
    Driver driver = driver(1L);
    Vehicle vehicle = vehicle(2L, driver, 4);
    givenOwnedVehicle(driver, vehicle);

    assertThatThrownBy(() -> tripService.create(request(5, validStops())))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Offered seats must be between 1 and 4");
    verify(tripRepository, never()).save(any());
  }

  @Test
  void create_rejectsVehicleOwnedByAnotherDriver() {
    Driver driver = driver(1L);
    Driver otherDriver = driver(9L);
    Vehicle vehicle = vehicle(2L, otherDriver, 4);
    givenOwnedVehicle(driver, vehicle);

    assertThatThrownBy(() -> tripService.create(request(3, validStops())))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Vehicle does not belong to driver 1");
    verify(tripRepository, never()).save(any());
  }

  private void givenOwnedVehicle(Driver driver, Vehicle vehicle) {
    when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
    when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
  }

  private TripRequestDTO request(Integer offeredSeats, List<TripStopRequestDTO> stops) {
    return new TripRequestDTO(1L, 2L, offeredSeats, stops);
  }

  private List<TripStopRequestDTO> validStops() {
    return List.of(
        stop("Rabat", DEPARTURE, "0"),
        stop("Casablanca", DEPARTURE.plusHours(1), "40"),
        stop("Settat", DEPARTURE.plusHours(2), "70"),
        stop("Marrakech", DEPARTURE.plusHours(4), "120"));
  }

  private TripStopRequestDTO stop(String city, LocalDateTime passingTime, String price) {
    return new TripStopRequestDTO(city, passingTime, new BigDecimal(price));
  }

  private Driver driver(Long id) {
    Driver driver = Driver.builder()
        .firstName("Hamid")
        .lastName("El Amrani")
        .email("hamid@example.com")
        .phone("0600000001")
        .licenseNumber("AB-123456")
        .build();
    ReflectionTestUtils.setField(driver, "id", id);
    return driver;
  }

  private Vehicle vehicle(Long id, Driver driver, int capacity) {
    Vehicle vehicle = Vehicle.builder()
        .brand("Dacia")
        .model("Logan")
        .licensePlate("12345-A-6")
        .color("White")
        .passengerCapacity(capacity)
        .driver(driver)
        .build();
    ReflectionTestUtils.setField(vehicle, "id", id);
    return vehicle;
  }
}
