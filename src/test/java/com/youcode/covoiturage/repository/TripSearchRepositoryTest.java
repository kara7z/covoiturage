package com.youcode.covoiturage.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.youcode.covoiturage.dto.TripSearchResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.entity.Reservation;
import com.youcode.covoiturage.entity.StopPoint;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.enums.ReservationStatus;
import com.youcode.covoiturage.enums.TripStatus;

import jakarta.persistence.EntityManager;

@DataJpaTest
class TripSearchRepositoryTest {

  private static final LocalDateTime DAY_ONE = LocalDateTime.of(2026, 12, 1, 17, 0);
  private static final PageRequest FIRST_PAGE = PageRequest.of(0, 10);

  @Autowired private TripRepository tripRepository;
  @Autowired private DriverRepository driverRepository;
  @Autowired private VehicleRepository vehicleRepository;
  @Autowired private PassengerRepository passengerRepository;
  @Autowired private ReservationRepository reservationRepository;
  @Autowired private EntityManager entityManager;

  private Driver driver;
  private Vehicle vehicle;

  @BeforeEach
  void setUp() {
    driver = driverRepository.save(Driver.builder()
        .firstName("Hamid")
        .lastName("El Amrani")
        .email("hamid@example.com")
        .phone("0600000001")
        .licenseNumber("AB-123456")
        .build());
    vehicle = vehicleRepository.save(Vehicle.builder()
        .brand("Dacia")
        .model("Logan")
        .licensePlate("12345-A-6")
        .color("White")
        .passengerCapacity(4)
        .driver(driver)
        .build());
  }

  @Test
  void search_matchesOnlyStopsInTheRightDirectionAndReturnsTheSegmentPrice() {
    Trip trip = trip(DAY_ONE, TripStatus.PUBLISHED, "Rabat", "Casablanca", "Marrakech");

    Page<TripSearchResponseDTO> forward = search("Rabat", "Marrakech");

    assertThat(forward.getContent()).singleElement().satisfies(result -> {
      assertThat(result.id()).isEqualTo(trip.getId());
      assertThat(result.driverName()).isEqualTo("Hamid El Amrani");
      assertThat(result.departureCity()).isEqualTo("Rabat");
      assertThat(result.arrivalCity()).isEqualTo("Marrakech");
      assertThat(result.departureTime()).isEqualTo(DAY_ONE);
      assertThat(result.arrivalTime()).isEqualTo(DAY_ONE.plusHours(2));
      assertThat(result.segmentPrice()).isEqualByComparingTo("80");
      assertThat(result.remainingSeats()).isEqualTo(3L);
    });

    assertThat(search("Marrakech", "Rabat").getContent())
        .isEmpty();
  }

  @Test
  void search_appliesMaxPriceToTheSearchedSegment() {
    trip(DAY_ONE, TripStatus.PUBLISHED, "Rabat", "Casablanca", "Marrakech");

    assertThat(search("Rabat", "Casablanca", new BigDecimal("39")).getContent()).isEmpty();
    assertThat(search("Rabat", "Casablanca", new BigDecimal("40")).getContent()).hasSize(1);
  }

  @Test
  void search_filtersBySegmentDepartureDate() {
    Trip firstDay = trip(DAY_ONE, TripStatus.PUBLISHED, "Rabat", "Marrakech");
    trip(DAY_ONE.plusDays(1), TripStatus.PUBLISHED, "Rabat", "Marrakech");

    Page<TripSearchResponseDTO> results = tripRepository.search("Rabat", "Marrakech",
        LocalDate.of(2026, 12, 1).atStartOfDay(),
        LocalDate.of(2026, 12, 2).atStartOfDay(),
        null, FIRST_PAGE);

    assertThat(results.getContent()).extracting(TripSearchResponseDTO::id)
        .containsExactly(firstDay.getId());
  }

  @Test
  void search_paginatesAndSortsByDepartureTime() {
    Trip earliest = trip(DAY_ONE, TripStatus.PUBLISHED, "Rabat", "Marrakech");
    Trip latest = trip(DAY_ONE.plusDays(2), TripStatus.PUBLISHED, "Rabat", "Marrakech");
    Trip middle = trip(DAY_ONE.plusDays(1), TripStatus.PUBLISHED, "Rabat", "Marrakech");

    Page<TripSearchResponseDTO> firstPage = search("Rabat", "Marrakech", PageRequest.of(0, 2));
    Page<TripSearchResponseDTO> secondPage = search("Rabat", "Marrakech", PageRequest.of(1, 2));

    assertThat(firstPage.getTotalElements()).isEqualTo(3);
    assertThat(firstPage.getContent()).extracting(TripSearchResponseDTO::id)
        .containsExactly(earliest.getId(), middle.getId());
    assertThat(secondPage.getContent()).extracting(TripSearchResponseDTO::id)
        .containsExactly(latest.getId());
  }

  @Test
  void search_excludesCancelledTripsAndTripsWithoutRemainingSeats() {
    Trip available = trip(DAY_ONE, TripStatus.PUBLISHED, "Rabat", "Marrakech");
    trip(DAY_ONE.plusHours(1), TripStatus.CANCELLED, "Rabat", "Marrakech");
    Trip full = trip(DAY_ONE.plusHours(2), TripStatus.PUBLISHED, "Rabat", "Marrakech");
    reserve(full, 3);

    Page<TripSearchResponseDTO> results = search("Rabat", "Marrakech");

    assertThat(results.getContent()).extracting(TripSearchResponseDTO::id)
        .containsExactly(available.getId());
    assertThat(results.getContent().get(0).remainingSeats()).isEqualTo(3L);
  }

  @Test
  void search_computesRemainingSeatsFromConfirmedReservationsOnly() {
    Trip trip = trip(DAY_ONE, TripStatus.PUBLISHED, "Rabat", "Marrakech");
    reserve(trip, 2);
    reserve(trip, 3, ReservationStatus.CANCELLED);

    Page<TripSearchResponseDTO> results = search("Rabat", "Marrakech");

    assertThat(results.getContent()).singleElement()
        .satisfies(result -> assertThat(result.remainingSeats()).isEqualTo(1L));
  }

  @Test
  void findDetailById_fetchesRouteDriverAndVehicleInOneQuery() {
    Trip saved = trip(DAY_ONE, TripStatus.PUBLISHED, "Rabat", "Casablanca", "Marrakech");
    entityManager.clear();
    Statistics statistics = entityManager.getEntityManagerFactory()
        .unwrap(SessionFactory.class).getStatistics();
    statistics.clear();

    Trip detail = tripRepository.findDetailById(saved.getId()).orElseThrow();

    assertThat(detail.getStopPoints()).extracting(StopPoint::getCity)
        .containsExactly("Rabat", "Casablanca", "Marrakech");
    assertThat(detail.getDriver().getFirstName()).isEqualTo("Hamid");
    assertThat(detail.getVehicle().getBrand()).isEqualTo("Dacia");
    assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
  }

  private Page<TripSearchResponseDTO> search(String departure, String arrival) {
    return tripRepository.search(departure, arrival, null, null, null, FIRST_PAGE);
  }

  private Page<TripSearchResponseDTO> search(String departure, String arrival, BigDecimal maxPrice) {
    return tripRepository.search(departure, arrival, null, null, maxPrice, FIRST_PAGE);
  }

  private Page<TripSearchResponseDTO> search(String departure, String arrival, PageRequest pageable) {
    return tripRepository.search(departure, arrival, null, null, null, pageable);
  }

  private Trip trip(LocalDateTime departure, TripStatus status, String... cities) {
    Trip trip = Trip.builder()
        .driver(driver)
        .vehicle(vehicle)
        .departureCity(cities[0])
        .arrivalCity(cities[cities.length - 1])
        .departureDate(departure)
        .offeredSeats(3)
        .status(status)
        .build();
    for (int order = 0; order < cities.length; order++) {
      trip.addStopPoint(StopPoint.builder()
          .city(cities[order])
          .stopOrder(order)
          .passingTime(departure.plusHours(order))
          .priceFromDeparture(BigDecimal.valueOf(order * 40L))
          .build());
    }
    return tripRepository.saveAndFlush(trip);
  }

  private void reserve(Trip trip, int seats) {
    reserve(trip, seats, ReservationStatus.CONFIRMED);
  }

  private void reserve(Trip trip, int seats, ReservationStatus status) {
    Passenger passenger = passengerRepository.save(Passenger.builder()
        .firstName("Sara")
        .lastName("Bennani")
        .email("sara" + seats + status.name() + "@example.com")
        .phone("0600000002")
        .build());
    reservationRepository.save(Reservation.builder()
        .passenger(passenger)
        .trip(trip)
        .boardingPoint(trip.getDepartureCity())
        .dropOffPoint(trip.getArrivalCity())
        .numberOfSeats(seats)
        .totalAmount(BigDecimal.valueOf(seats * 40L))
        .reservationDate(LocalDateTime.now())
        .status(status)
        .build());
    reservationRepository.flush();
  }
}
