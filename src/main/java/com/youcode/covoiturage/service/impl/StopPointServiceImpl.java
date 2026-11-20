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
import lombok.extern.slf4j.Slf4j;

/**
 * StopPointServiceImpl
 */
@Slf4j
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
    log.info("Creating stop point for trip id {} in city {}", requestDTO.tripId(), requestDTO.city());
    Trip trip = findTrip(requestDTO.tripId());
    StopPoint stopPoint = stopPointMapper.toEntity(requestDTO, trip);
    StopPointResponseDTO response = stopPointMapper.toResponseDTO(stopPointRepository.save(stopPoint));
    log.info("Stop point created with id {}", response.id());
    return response;
  }

  @Override
  public StopPointResponseDTO findById(Long id) {
    log.debug("Fetching stop point with id {}", id);
    return stopPointMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<StopPointResponseDTO> findAll() {
    log.debug("Fetching all stop points");
    return stopPointRepository.findAll().stream()
        .map(stopPointMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public StopPointResponseDTO update(Long id, StopPointRequestDTO requestDTO) {
    log.info("Updating stop point with id {}", id);
    StopPoint stopPoint = findEntity(id);
    Trip trip = findTrip(requestDTO.tripId());
    stopPointMapper.updateEntity(stopPoint, requestDTO, trip);
    StopPointResponseDTO response = stopPointMapper.toResponseDTO(stopPointRepository.save(stopPoint));
    log.info("Stop point updated with id {}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.info("Deleting stop point with id {}", id);
    stopPointRepository.delete(findEntity(id));
    log.info("Stop point deleted with id {}", id);
  }

  private StopPoint findEntity(Long id) {
    return stopPointRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Stop point not found with id {}", id);
          return new ResourceNotFoundException("Stop point not found with id " + id);
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
