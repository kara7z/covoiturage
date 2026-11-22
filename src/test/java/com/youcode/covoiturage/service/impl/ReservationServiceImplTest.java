package com.youcode.covoiturage.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.entity.Reservation;
import com.youcode.covoiturage.entity.StopPoint;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.enums.ReservationStatus;
import com.youcode.covoiturage.enums.TripStatus;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.ReservationMapper;
import com.youcode.covoiturage.repository.PassengerRepository;
import com.youcode.covoiturage.repository.ReservationRepository;
import com.youcode.covoiturage.repository.TripRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 12, 1, 10, 0);

  @Mock
  private TripRepository tripRepository;

  @Mock
  private PassengerRepository passengerRepository;

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private ReservationMapper reservationMapper;

  @InjectMocks
  private ReservationServiceImpl reservationService;

  @Test
  void create_computesSegmentPriceAndSavesConfirmedReservation() {
    Passenger passenger = passenger(1L);
    Trip trip = tripWithStops(10L, TripStatus.PUBLISHED, "Rabat", "Casablanca", "Marrakech");

    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(tripRepository.findDetailById(10L)).thenReturn(Optional.of(trip));
    when(reservationRepository.sumConfirmedSeatsByTripId(10L)).thenReturn(0L);
    when(reservationRepository.existsByPassengerIdAndTripIdAndStatus(
        eq(1L), eq(10L), eq(ReservationStatus.CONFIRMED))).thenReturn(false);

    Reservation saved = reservationWithId(100L, passenger, trip);
    when(reservationRepository.save(any())).thenReturn(saved);

    ReservationResponseDTO expected = new ReservationResponseDTO(
        100L, 1L, 10L, "Rabat", "Marrakech", 2, new BigDecimal("160.00"), NOW, ReservationStatus.CONFIRMED);
    when(reservationMapper.toResponseDTO(saved)).thenReturn(expected);

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 10L, "Rabat", "Marrakech", 2, null, null, null);

    ReservationResponseDTO response = reservationService.create(request);

    assertThat(response).isEqualTo(expected);
    assertThat(response.totalAmount()).isEqualByComparingTo("160.00");
    assertThat(response.status()).isEqualTo(ReservationStatus.CONFIRMED);
    verify(reservationRepository).save(any());
  }

  @Test
  void create_rejectsUnpublishedTrip() {
    Passenger passenger = passenger(1L);
    Trip trip = tripWithStops(10L, TripStatus.COMPLETED, "Rabat", "Marrakech");

    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(tripRepository.findDetailById(10L)).thenReturn(Optional.of(trip));

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 10L, "Rabat", "Marrakech", 1, null, null, null);

    assertThatThrownBy(() -> reservationService.create(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Trip 10 is not published");
  }

  @Test
  void create_rejectsUnknownBoardingPoint() {
    Passenger passenger = passenger(1L);
    Trip trip = tripWithStops(10L, TripStatus.PUBLISHED, "Rabat", "Marrakech");

    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(tripRepository.findDetailById(10L)).thenReturn(Optional.of(trip));

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 10L, "Unknown", "Marrakech", 1, null, null, null);

    assertThatThrownBy(() -> reservationService.create(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("boarding point Unknown is not a stop on this trip");
  }

  @Test
  void create_rejectsUnknownDropOffPoint() {
    Passenger passenger = passenger(1L);
    Trip trip = tripWithStops(10L, TripStatus.PUBLISHED, "Rabat", "Marrakech");

    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(tripRepository.findDetailById(10L)).thenReturn(Optional.of(trip));

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 10L, "Rabat", "Unknown", 1, null, null, null);

    assertThatThrownBy(() -> reservationService.create(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("drop off point Unknown is not a stop on this trip");
  }

  @Test
  void create_rejectsReversedStops() {
    Passenger passenger = passenger(1L);
    Trip trip = tripWithStops(10L, TripStatus.PUBLISHED, "Rabat", "Marrakech");

    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(tripRepository.findDetailById(10L)).thenReturn(Optional.of(trip));

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 10L, "Marrakech", "Rabat", 1, null, null, null);

    assertThatThrownBy(() -> reservationService.create(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Boarding stop must come before drop off stop");
  }

  @Test
  void create_rejectsNotEnoughSeats() {
    Passenger passenger = passenger(1L);
    Trip trip = tripWithStops(10L, TripStatus.PUBLISHED, "Rabat", "Marrakech");

    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(tripRepository.findDetailById(10L)).thenReturn(Optional.of(trip));
    when(reservationRepository.sumConfirmedSeatsByTripId(10L)).thenReturn(3L);

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 10L, "Rabat", "Marrakech", 1, null, null, null);

    assertThatThrownBy(() -> reservationService.create(request))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessage("Not enough seats available. Remaining: 0");
  }

  @Test
  void create_rejectsDuplicateReservation() {
    Passenger passenger = passenger(1L);
    Trip trip = tripWithStops(10L, TripStatus.PUBLISHED, "Rabat", "Marrakech");

    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(tripRepository.findDetailById(10L)).thenReturn(Optional.of(trip));
    when(reservationRepository.sumConfirmedSeatsByTripId(10L)).thenReturn(0L);
    when(reservationRepository.existsByPassengerIdAndTripIdAndStatus(
        eq(1L), eq(10L), eq(ReservationStatus.CONFIRMED))).thenReturn(true);

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 10L, "Rabat", "Marrakech", 1, null, null, null);

    assertThatThrownBy(() -> reservationService.create(request))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessage("Passenger 1 already has a confirmed reservation on trip 10");
  }

  @Test
  void create_setsTripToFullWhenNoSeatsRemaining() {
    Passenger passenger = passenger(1L);
    Trip trip = tripWithStops(10L, TripStatus.PUBLISHED, "Rabat", "Marrakech");
    trip.setOfferedSeats(1);

    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(tripRepository.findDetailById(10L)).thenReturn(Optional.of(trip));
    when(reservationRepository.sumConfirmedSeatsByTripId(10L)).thenReturn(0L).thenReturn(1L);
    when(reservationRepository.existsByPassengerIdAndTripIdAndStatus(
        eq(1L), eq(10L), eq(ReservationStatus.CONFIRMED))).thenReturn(false);

    Reservation saved = reservationWithId(100L, passenger, trip);
    when(reservationRepository.save(any())).thenReturn(saved);
    when(reservationMapper.toResponseDTO(saved)).thenReturn(
        new ReservationResponseDTO(100L, 1L, 10L, "Rabat", "Marrakech", 1,
            new BigDecimal("80.00"), NOW, ReservationStatus.CONFIRMED));

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 10L, "Rabat", "Marrakech", 1, null, null, null);

    reservationService.create(request);

    assertThat(trip.getStatus()).isEqualTo(TripStatus.FULL);
    verify(tripRepository).save(trip);
  }

  @Test
  void create_rejectsUnknownPassenger() {
    when(passengerRepository.findById(99L)).thenReturn(Optional.empty());

    ReservationRequestDTO request = new ReservationRequestDTO(
        99L, 10L, "Rabat", "Marrakech", 1, null, null, null);

    assertThatThrownBy(() -> reservationService.create(request))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void create_rejectsUnknownTrip() {
    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger(1L)));
    when(tripRepository.findDetailById(99L)).thenReturn(Optional.empty());

    ReservationRequestDTO request = new ReservationRequestDTO(
        1L, 99L, "Rabat", "Marrakech", 1, null, null, null);

    assertThatThrownBy(() -> reservationService.create(request))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  private Passenger passenger(Long id) {
    Passenger passenger = Passenger.builder()
        .firstName("Sara")
        .lastName("Bennani")
        .email("sara@example.com")
        .phone("0600000002")
        .build();
    ReflectionTestUtils.setField(passenger, "id", id);
    return passenger;
  }

  private Trip tripWithStops(Long id, TripStatus status, String... cities) {
    Trip trip = Trip.builder()
        .driver(driver(1L))
        .vehicle(vehicle(2L, driver(1L), 4))
        .departureCity(cities[0])
        .arrivalCity(cities[cities.length - 1])
        .departureDate(NOW)
        .offeredSeats(3)
        .status(status)
        .build();
    LocalDateTime time = NOW;
    BigDecimal price = BigDecimal.ZERO;
    for (int order = 0; order < cities.length; order++) {
      trip.addStopPoint(StopPoint.builder()
          .city(cities[order])
          .stopOrder(order)
          .passingTime(time)
          .priceFromDeparture(price)
          .build());
      time = time.plusHours(1);
      price = price.add(BigDecimal.valueOf(40L));
    }
    ReflectionTestUtils.setField(trip, "id", id);
    return trip;
  }

  private Reservation reservationWithId(Long id, Passenger passenger, Trip trip) {
    Reservation reservation = Reservation.builder()
        .passenger(passenger)
        .trip(trip)
        .boardingPoint("Rabat")
        .dropOffPoint("Marrakech")
        .numberOfSeats(2)
        .totalAmount(new BigDecimal("160.00"))
        .reservationDate(NOW)
        .status(ReservationStatus.CONFIRMED)
        .build();
    ReflectionTestUtils.setField(reservation, "id", id);
    return reservation;
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

  private Vehicle vehicle(Long id,
      Driver driver, int capacity) {
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