package com.youcode.covoiturage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.StopPointRequestDTO;
import com.youcode.covoiturage.dto.StopPointResponseDTO;
import com.youcode.covoiturage.entity.StopPoint;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.StopPointMapper;
import com.youcode.covoiturage.repository.StopPointRepository;
import com.youcode.covoiturage.repository.TripRepository;
import com.youcode.covoiturage.service.StopPointService;

import lombok.RequiredArgsConstructor;

/**
 * StopPointServiceImpl
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StopPointServiceImpl implements StopPointService {

  private final StopPointRepository stopPointRepository;
  private final TripRepository tripRepository;
  private final StopPointMapper stopPointMapper;

  @Override
  @Transactional
  public StopPointResponseDTO create(StopPointRequestDTO requestDTO) {
    Trip trip = findTrip(requestDTO.tripId());
    StopPoint stopPoint = stopPointMapper.toEntity(requestDTO, trip);
    return stopPointMapper.toResponseDTO(stopPointRepository.save(stopPoint));
  }

  @Override
  public StopPointResponseDTO findById(Long id) {
    return stopPointMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<StopPointResponseDTO> findAll() {
    return stopPointRepository.findAll().stream()
        .map(stopPointMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public StopPointResponseDTO update(Long id, StopPointRequestDTO requestDTO) {
    StopPoint stopPoint = findEntity(id);
    Trip trip = findTrip(requestDTO.tripId());
    stopPointMapper.updateEntity(stopPoint, requestDTO, trip);
    return stopPointMapper.toResponseDTO(stopPointRepository.save(stopPoint));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    stopPointRepository.delete(findEntity(id));
  }

  private StopPoint findEntity(Long id) {
    return stopPointRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Stop point not found with id " + id));
  }

  private Trip findTrip(Long id) {
    return tripRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + id));
  }
}
