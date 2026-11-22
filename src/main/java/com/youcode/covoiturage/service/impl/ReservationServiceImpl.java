package com.youcode.covoiturage.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.entity.Reservation;
import com.youcode.covoiturage.entity.StopPoint;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.enums.ReservationStatus;
import com.youcode.covoiturage.enums.TripStatus;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.ReservationMapper;
import com.youcode.covoiturage.repository.PassengerRepository;
import com.youcode.covoiturage.repository.ReservationRepository;
import com.youcode.covoiturage.repository.TripRepository;
import com.youcode.covoiturage.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ReservationServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final PassengerRepository passengerRepository;
  private final TripRepository tripRepository;
  private final ReservationMapper reservationMapper;

  @Override
  @Transactional
  public ReservationResponseDTO create(ReservationRequestDTO requestDTO) {
    log.info("Creating reservation for passenger id {} on trip id {}", requestDTO.passengerId(),
        requestDTO.tripId());
    Passenger passenger = findPassenger(requestDTO.passengerId());
    Trip trip = findTrip(requestDTO.tripId());

    validateTripIsPublished(trip);

    StopPoint boardingStop = findStopPoint(trip, requestDTO.boardingPoint(), "boarding");
    StopPoint dropOffStop = findStopPoint(trip, requestDTO.dropOffPoint(), "drop off");

    validateBoardingBeforeDropOff(boardingStop, dropOffStop);

    BigDecimal segmentPrice = dropOffStop.getPriceFromDeparture()
        .subtract(boardingStop.getPriceFromDeparture());
    BigDecimal amount = segmentPrice
        .multiply(BigDecimal.valueOf(requestDTO.numberOfSeats()))
        .setScale(2, RoundingMode.HALF_UP);

    validateSeatsAvailable(trip, requestDTO.numberOfSeats());
    validateNoDuplicateReservation(passenger, trip);

    Reservation reservation = Reservation.builder()
        .passenger(passenger)
        .trip(trip)
        .boardingPoint(requestDTO.boardingPoint())
        .dropOffPoint(requestDTO.dropOffPoint())
        .numberOfSeats(requestDTO.numberOfSeats())
        .totalAmount(amount)
        .reservationDate(LocalDateTime.now())
        .status(ReservationStatus.CONFIRMED)
        .build();

    reservation = reservationRepository.save(reservation);

    updateTripStatusIfFull(trip);

    log.info("Reservation created with id {}", reservation.getId());
    return reservationMapper.toResponseDTO(reservation);
  }

  @Override
  public ReservationResponseDTO findById(Long id) {
    log.debug("Fetching reservation with id {}", id);
    return reservationMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<ReservationResponseDTO> findAll() {
    log.debug("Fetching all reservations");
    return reservationRepository.findAll().stream()
        .map(reservationMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public ReservationResponseDTO update(Long id, ReservationRequestDTO requestDTO) {
    log.info("Updating reservation with id {}", id);
    Reservation reservation = findEntity(id);
    Passenger passenger = findPassenger(requestDTO.passengerId());
    Trip trip = findTrip(requestDTO.tripId());
    reservationMapper.updateEntity(reservation, requestDTO, passenger, trip);
    ReservationResponseDTO response = reservationMapper.toResponseDTO(reservationRepository.save(reservation));
    log.info("Reservation updated with id {}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.info("Deleting reservation with id {}", id);
    reservationRepository.delete(findEntity(id));
    log.info("Reservation deleted with id {}", id);
  }

  private Reservation findEntity(Long id) {
    return reservationRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Reservation not found with id {}", id);
          return new ResourceNotFoundException("Reservation not found with id " + id);
        });
  }

  private Passenger findPassenger(Long id) {
    return passengerRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Passenger not found with id {}", id);
          return new ResourceNotFoundException("Passenger not found with id " + id);
        });
  }

  private Trip findTrip(Long id) {
    return tripRepository.findDetailById(id)
        .orElseThrow(() -> {
          log.warn("Trip not found with id {}", id);
          return new ResourceNotFoundException("Trip not found with id " + id);
        });
  }

  private void validateTripIsPublished(Trip trip) {
    if (trip.getStatus() != TripStatus.PUBLISHED) {
      throw new BadRequestException("TRIP_NOT_PUBLISHED",
          "Trip " + trip.getId() + " is not published");
    }
  }

  private StopPoint findStopPoint(Trip trip, String city, String label) {
    return trip.getStopPoints().stream()
        .filter(sp -> sp.getCity().equalsIgnoreCase(city))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("INVALID_" + label.toUpperCase() + "_POINT",
            label + " point " + city + " is not a stop on this trip"));
  }

  private void validateBoardingBeforeDropOff(StopPoint boarding, StopPoint dropOff) {
    if (boarding.getStopOrder() >= dropOff.getStopOrder()) {
      throw new BadRequestException("REVERSED_STOPS",
          "Boarding stop must come before drop off stop");
    }
  }

  private void validateSeatsAvailable(Trip trip, int numberOfSeats) {
    Long confirmedSeats = reservationRepository.sumConfirmedSeatsByTripId(trip.getId());
    long remainingSeats = Math.max(trip.getOfferedSeats() - confirmedSeats, 0);
    if (numberOfSeats > remainingSeats) {
      throw new BusinessRuleException("TRIP_FULL",
          "Not enough seats available. Remaining: " + remainingSeats);
    }
  }

  private void validateNoDuplicateReservation(Passenger passenger, Trip trip) {
    if (reservationRepository.existsByPassengerIdAndTripIdAndStatus(
        passenger.getId(), trip.getId(), ReservationStatus.CONFIRMED)) {
      throw new BusinessRuleException("DUPLICATE_RESERVATION",
          "Passenger " + passenger.getId() + " already has a confirmed reservation on trip " + trip.getId());
    }
  }

  private void updateTripStatusIfFull(Trip trip) {
    Long confirmedSeats = reservationRepository.sumConfirmedSeatsByTripId(trip.getId());
    long remainingSeats = Math.max(trip.getOfferedSeats() - confirmedSeats, 0);
    if (remainingSeats == 0 && trip.getStatus() == TripStatus.PUBLISHED) {
      trip.setStatus(TripStatus.FULL);
      tripRepository.save(trip);
    }
  }
}
