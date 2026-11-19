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

/**
 * PassengerServiceImpl
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerServiceImpl implements PassengerService {

  private final PassengerRepository passengerRepository;
  private final PassengerMapper passengerMapper;

  @Override
  @Transactional
  public PassangerResponseDTO create(PassangerRequestDTO requestDTO) {
    Passenger passenger = passengerMapper.toEntity(requestDTO);
    return passengerMapper.toResponseDTO(passengerRepository.save(passenger));
  }

  @Override
  public PassangerResponseDTO findById(Long id) {
    return passengerMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<PassangerResponseDTO> findAll() {
    return passengerRepository.findAll().stream()
        .map(passengerMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public PassangerResponseDTO update(Long id, PassangerRequestDTO requestDTO) {
    Passenger passenger = findEntity(id);
    passengerMapper.updateEntity(passenger, requestDTO);
    return passengerMapper.toResponseDTO(passengerRepository.save(passenger));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    passengerRepository.delete(findEntity(id));
  }

  private Passenger findEntity(Long id) {
    return passengerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id " + id));
  }
}
