package com.youcode.covoiturage.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.PassangerRequestDTO;
import com.youcode.covoiturage.dto.PassangerResponseDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.exception.AlreadyExistsException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.PassengerMapper;
import com.youcode.covoiturage.mapper.ReservationMapper;
import com.youcode.covoiturage.repository.PassengerRepository;
import com.youcode.covoiturage.repository.ReservationRepository;
import com.youcode.covoiturage.service.PassengerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PassengerServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerServiceImpl implements PassengerService {

  private final PassengerRepository passengerRepository;
  private final PassengerMapper passengerMapper;
  private final ReservationRepository reservationRepository;
  private final ReservationMapper reservationMapper;

  @Override
  @Transactional
  public PassangerResponseDTO create(PassangerRequestDTO requestDTO) {
    log.info("Creating passenger with email {}", requestDTO.email());
    ensureEmailAvailable(requestDTO.email(), null);
    Passenger passenger = passengerMapper.toEntity(requestDTO);
    PassangerResponseDTO response = passengerMapper.toResponseDTO(passengerRepository.save(passenger));
    log.info("Passenger created with id {}", response.id());
    return response;
  }

  @Override
  public PassangerResponseDTO findById(Long id) {
    log.debug("Fetching passenger with id {}", id);
    return passengerMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<PassangerResponseDTO> findAll() {
    log.debug("Fetching all passengers");
    return passengerRepository.findAll().stream()
        .map(passengerMapper::toResponseDTO)
        .toList();
  }

  @Override
  public PassangerResponseDTO findByEmail(String email) {
    log.debug("Fetching passenger with email {}", email);
    return passengerRepository.findByEmail(email)
        .map(passengerMapper::toResponseDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with email " + email));
  }

  @Override
  public Page<ReservationResponseDTO> findReservationHistory(Long passengerId, Pageable pageable) {
    findEntity(passengerId);
    Pageable dateSortedPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        Sort.by(Sort.Direction.DESC, "reservationDate"));
    return reservationRepository.findByPassengerId(passengerId, dateSortedPage)
        .map(reservationMapper::toResponseDTO);
  }

  @Override
  @Transactional
  public PassangerResponseDTO update(Long id, PassangerRequestDTO requestDTO) {
    log.info("Updating passenger with id {}", id);
    Passenger passenger = findEntity(id);
    ensureEmailAvailable(requestDTO.email(), id);
    passengerMapper.updateEntity(passenger, requestDTO);
    PassangerResponseDTO response = passengerMapper.toResponseDTO(passengerRepository.save(passenger));
    log.info("Passenger updated with id {}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.info("Deleting passenger with id {}", id);
    passengerRepository.delete(findEntity(id));
    log.info("Passenger deleted with id {}", id);
  }

  private Passenger findEntity(Long id) {
    return passengerRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Passenger not found with id {}", id);
          return new ResourceNotFoundException("Passenger not found with id " + id);
        });
  }

  private void ensureEmailAvailable(String email, Long currentId) {
    boolean exists = currentId == null
        ? passengerRepository.existsByEmail(email)
        : passengerRepository.existsByEmailAndIdNot(email, currentId);
    if (exists) {
      log.warn("Passenger email already in use: {}", email);
      throw new AlreadyExistsException("Passenger with email " + email + " already exists");
    }
  }
}
