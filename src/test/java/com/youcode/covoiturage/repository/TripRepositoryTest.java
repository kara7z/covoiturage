package com.youcode.covoiturage.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.StopPoint;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.enums.TripStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import jakarta.persistence.EntityManager;

@DataJpaTest
class TripRepositoryTest {
  @Autowired private TripRepository tripRepository;
  @Autowired private StopPointRepository stopPointRepository;
  @Autowired private DriverRepository driverRepository;
  @Autowired private VehicleRepository vehicleRepository;
  @Autowired private EntityManager entityManager;

  @Test
  void persistsStopsInOrderAndDeletesThemWithTrip() {
    Driver driver = driverRepository.save(Driver.builder()
        .firstName("A")
        .lastName("Driver")
        .email("driver@example.com")
        .phone("0600000000")
        .licenseNumber("LIC-1")
        .build());
    Vehicle vehicle = vehicleRepository.save(Vehicle.builder()
        .brand("Brand")
        .model("Model")
        .licensePlate("PLATE-1")
        .color("Blue")
        .passengerCapacity(4)
        .driver(driver)
        .build());

    Trip trip = Trip.builder()
        .driver(driver)
        .vehicle(vehicle)
        .departureCity("Alpha")
        .arrivalCity("Gamma")
        .departureDate(LocalDateTime.of(2026, 11, 20, 9, 0))
        .offeredSeats(3)
        .status(TripStatus.PUBLISHED)
        .build();
    trip.addStopPoint(stop("Gamma", 2));
    trip.addStopPoint(stop("Alpha", 0));
    trip.addStopPoint(stop("Beta", 1));

    Trip saved = tripRepository.saveAndFlush(trip);
    Long tripId = saved.getId();
    tripRepository.flush();
    entityManager.clear();

    Trip reloaded = tripRepository.findById(tripId).orElseThrow();
    assertThat(reloaded.getStopPoints()).extracting(StopPoint::getCity)
        .containsExactly("Alpha", "Beta", "Gamma");

    tripRepository.delete(reloaded);
    tripRepository.flush();
    assertThat(stopPointRepository.findAll()).isEmpty();
  }

  private StopPoint stop(String city, int order) {
    return StopPoint.builder()
        .city(city)
        .stopOrder(order)
        .passingTime(LocalDateTime.of(2026, 11, 20, 9, 0).plusHours(order))
        .priceFromDeparture(BigDecimal.valueOf(order * 10L))
        .build();
  }
}
