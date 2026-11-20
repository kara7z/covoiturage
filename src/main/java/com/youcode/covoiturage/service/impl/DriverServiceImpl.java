package com.youcode.covoiturage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.covoiturage.dto.DriverRequestDTO;
import com.youcode.covoiturage.dto.DriverResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.DriverMapper;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.service.DriverService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DriverServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverServiceImpl implements DriverService {

  private final DriverRepository driverRepository;
  private final DriverMapper driverMapper;

  @Override
  @Transactional
  public DriverResponseDTO create(DriverRequestDTO requestDTO) {
    log.info("Creating driver with email {}", requestDTO.email());
    Driver driver = driverMapper.toEntity(requestDTO);
    DriverResponseDTO response = driverMapper.toResponseDTO(driverRepository.save(driver));
    log.info("Driver created with id {}", response.id());
    return response;
  }

  @Override
  public DriverResponseDTO findById(Long id) {
    log.debug("Fetching driver with id {}", id);
    return driverMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<DriverResponseDTO> findAll() {
    log.debug("Fetching all drivers");
    return driverRepository.findAll().stream()
        .map(driverMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public DriverResponseDTO update(Long id, DriverRequestDTO requestDTO) {
    log.info("Updating driver with id {}", id);
    Driver driver = findEntity(id);
    driverMapper.updateEntity(driver, requestDTO);
    DriverResponseDTO response = driverMapper.toResponseDTO(driverRepository.save(driver));
    log.info("Driver updated with id {}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.info("Deleting driver with id {}", id);
    driverRepository.delete(findEntity(id));
    log.info("Driver deleted with id {}", id);
  }

  private Driver findEntity(Long id) {
    return driverRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Driver not found with id {}", id);
          return new ResourceNotFoundException("Driver not found with id " + id);
        });
  }
}
