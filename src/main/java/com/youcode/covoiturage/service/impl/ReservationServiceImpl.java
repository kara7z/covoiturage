package com.youcode.covoiturage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.entity.Reservation;
import com.youcode.covoiturage.entity.Trip;
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
    Reservation reservation = reservationMapper.toEntity(requestDTO, passenger, trip);
    ReservationResponseDTO response = reservationMapper.toResponseDTO(reservationRepository.save(reservation));
    log.info("Reservation created with id {}", response.id());
    return response;
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
    return tripRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Trip not found with id {}", id);
          return new ResourceNotFoundException("Trip not found with id " + id);
        });
  }
}
