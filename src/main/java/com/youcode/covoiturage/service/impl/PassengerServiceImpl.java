package com.youcode.covoiturage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.PassangerRequestDTO;
import com.youcode.covoiturage.dto.PassangerResponseDTO;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.PassengerMapper;
import com.youcode.covoiturage.repository.PassengerRepository;
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

  @Override
  @Transactional
  public PassangerResponseDTO create(PassangerRequestDTO requestDTO) {
    log.info("Creating passenger with email {}", requestDTO.email());
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
  @Transactional
  public PassangerResponseDTO update(Long id, PassangerRequestDTO requestDTO) {
    log.info("Updating passenger with id {}", id);
    Passenger passenger = findEntity(id);
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
}
