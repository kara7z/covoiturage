package com.youcode.covoiturage.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.youcode.covoiturage.dto.DriverRequestDTO;
import com.youcode.covoiturage.dto.DriverResponseDTO;
import com.youcode.covoiturage.dto.PassangerRequestDTO;
import com.youcode.covoiturage.dto.PassangerResponseDTO;
import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.dto.StopPointRequestDTO;
import com.youcode.covoiturage.dto.StopPointResponseDTO;
import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.dto.VehicleRequestDTO;
import com.youcode.covoiturage.dto.VehicleResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.entity.Reservation;
import com.youcode.covoiturage.entity.StopPoint;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.enums.ReservationStatus;
import com.youcode.covoiturage.enums.TripStatus;

class MapperTest {

  private final DriverMapper driverMapper = new DriverMapperImpl();
  private final PassengerMapper passengerMapper = new PassengerMapperImpl();
  private final VehicleMapper vehicleMapper = new VehicleMapperImpl();
  private final TripMapper tripMapper = new TripMapperImpl();
  private final StopPointMapper stopPointMapper = new StopPointMapperImpl();
  private final ReservationMapper reservationMapper = new ReservationMapperImpl();

  private Driver driver() {
    Driver driver = Driver.builder()
        .firstName("Youssef")
        .lastName("El Amrani")
        .email("youssef@example.com")
        .phone("0600000000")
        .licenseNumber("L-123")
        .build();
    ReflectionTestUtils.setField(driver, "id", 1L);
    return driver;
  }

  private Passenger passenger() {
    Passenger passenger = Passenger.builder()
        .firstName("Sara")
        .lastName("Bennis")
        .email("sara@example.com")
        .phone("0700000000")
        .build();
    ReflectionTestUtils.setField(passenger, "id", 2L);
    return passenger;
  }

  private Vehicle vehicle(Driver driver) {
    Vehicle vehicle = Vehicle.builder()
        .brand("Dacia")
        .model("Logan")
        .licensePlate("12345-A-1")
        .color("White")
        .passengerCapacity(4)
        .driver(driver)
        .build();
    ReflectionTestUtils.setField(vehicle, "id", 3L);
    return vehicle;
  }

  private Trip trip(Driver driver, Vehicle vehicle) {
    Trip trip = new Trip(driver, vehicle, "Rabat", "Marrakech",
        LocalDateTime.of(2026, 12, 1, 8, 0), 3, TripStatus.PUBLISHED, null);
    ReflectionTestUtils.setField(trip, "id", 4L);
    return trip;
  }

  @Test
  void driverMapsToEntityAndResponse() {
    Driver created = driverMapper.toEntity(
        new DriverRequestDTO("Hamid", "Zahraoui", "hamid@example.com", "0611111111", "L-999"));
    assertEquals("Hamid", created.getFirstName());
    assertEquals("Zahraoui", created.getLastName());
    assertEquals("L-999", created.getLicenseNumber());

    DriverResponseDTO response = driverMapper.toResponseDTO(driver());
    assertEquals(1L, response.id());
    assertEquals("Youssef El Amrani", response.fullName());
    assertEquals("L-123", response.licenseNumber());
  }

  @Test
  void passengerMapsToEntityAndResponse() {
    Passenger created = passengerMapper.toEntity(
        new PassangerRequestDTO("Nadia", "Alaoui", "nadia@example.com", "0622222222"));
    assertEquals("Nadia", created.getFirstName());
    assertEquals("Alaoui", created.getLastName());

    PassangerResponseDTO response = passengerMapper.toResponseDTO(passenger());
    assertEquals(2L, response.id());
    assertEquals("Sara Bennis", response.fullName());
  }

  @Test
  void vehicleMapsDriverRelationAndId() {
    Driver driver = driver();
    Vehicle entity = vehicleMapper.toEntity(
        new VehicleRequestDTO("Renault", "Clio", "54321-B-2", "Black", 5, 1L), driver);
    assertSame(driver, entity.getDriver());
    assertEquals(5, entity.getPassengerCapacity());

    VehicleResponseDTO response = vehicleMapper.toResponseDTO(vehicle(driver));
    assertEquals(3L, response.id());
    assertEquals(1L, response.driverId());
    assertEquals("12345-A-1", response.licensePlate());
  }

  @Test
  void tripMapsRelationsStatusAndEmptyStopPoints() {
    Driver driver = driver();
    Vehicle vehicle = vehicle(driver);
    Trip entity = tripMapper.toEntity(
        new TripRequestDTO(1L, 3L, "Rabat", "Marrakech",
            LocalDateTime.of(2026, 12, 1, 8, 0), 3, TripStatus.PUBLISHED),
        driver, vehicle);
    assertSame(driver, entity.getDriver());
    assertSame(vehicle, entity.getVehicle());
    assertEquals(TripStatus.PUBLISHED, entity.getStatus());
    assertNotNull(entity.getStopPoints());
    assertTrue(entity.getStopPoints().isEmpty());

    TripResponseDTO response = tripMapper.toResponseDTO(trip(driver, vehicle));
    assertEquals(4L, response.id());
    assertEquals(1L, response.driverId());
    assertEquals(3L, response.vehicleId());
    assertEquals(TripStatus.PUBLISHED, response.status());
  }

  @Test
  void stopPointMapsTripRelationAndPrice() {
    Trip trip = trip(driver(), vehicle(driver()));
    StopPoint entity = stopPointMapper.toEntity(
        new StopPointRequestDTO(4L, "Casablanca", 2, LocalDateTime.of(2026, 12, 1, 10, 30),
            new BigDecimal("150.00")),
        trip);
    assertSame(trip, entity.getTrip());
    assertEquals(new BigDecimal("150.00"), entity.getPriceFromDeparture());

    StopPointResponseDTO response = stopPointMapper.toResponseDTO(entity);
    assertEquals(4L, response.tripId());
    assertEquals("Casablanca", response.city());
  }

  @Test
  void reservationMapsPassengerAndTripRelations() {
    Passenger passenger = passenger();
    Trip trip = trip(driver(), vehicle(driver()));
    Reservation entity = reservationMapper.toEntity(
        new ReservationRequestDTO(2L, 4L, "Rabat", "Marrakech", 2, new BigDecimal("300.00"),
            LocalDateTime.of(2026, 11, 20, 9, 0), ReservationStatus.CONFIRMED),
        passenger, trip);
    assertSame(passenger, entity.getPassenger());
    assertSame(trip, entity.getTrip());
    assertEquals(ReservationStatus.CONFIRMED, entity.getStatus());

    ReservationResponseDTO response = reservationMapper.toResponseDTO(entity);
    assertEquals(2L, response.passengerId());
    assertEquals(4L, response.tripId());
    assertEquals(new BigDecimal("300.00"), response.totalAmount());
  }
}
