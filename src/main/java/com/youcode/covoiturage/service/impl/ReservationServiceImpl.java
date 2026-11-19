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

/**
 * ReservationServiceImpl
 */
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
    Passenger passenger = findPassenger(requestDTO.passengerId());
    Trip trip = findTrip(requestDTO.tripId());
    Reservation reservation = reservationMapper.toEntity(requestDTO, passenger, trip);
    return reservationMapper.toResponseDTO(reservationRepository.save(reservation));
  }

  @Override
  public ReservationResponseDTO findById(Long id) {
    return reservationMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<ReservationResponseDTO> findAll() {
    return reservationRepository.findAll().stream()
        .map(reservationMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public ReservationResponseDTO update(Long id, ReservationRequestDTO requestDTO) {
    Reservation reservation = findEntity(id);
    Passenger passenger = findPassenger(requestDTO.passengerId());
    Trip trip = findTrip(requestDTO.tripId());
    reservationMapper.updateEntity(reservation, requestDTO, passenger, trip);
    return reservationMapper.toResponseDTO(reservationRepository.save(reservation));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    reservationRepository.delete(findEntity(id));
  }

  private Reservation findEntity(Long id) {
    return reservationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id " + id));
  }

  private Passenger findPassenger(Long id) {
    return passengerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id " + id));
  }

  private Trip findTrip(Long id) {
    return tripRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + id));
  }
}
