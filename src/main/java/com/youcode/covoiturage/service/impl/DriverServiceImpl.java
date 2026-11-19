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

/**
 * DriverServiceImpl
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverServiceImpl implements DriverService {

  private final DriverRepository driverRepository;
  private final DriverMapper driverMapper;

  @Override
  @Transactional
  public DriverResponseDTO create(DriverRequestDTO requestDTO) {
    Driver driver = driverMapper.toEntity(requestDTO);
    return driverMapper.toResponseDTO(driverRepository.save(driver));
  }

  @Override
  public DriverResponseDTO findById(Long id) {
    return driverMapper.toResponseDTO(findEntity(id));
  }

  @Override
  public List<DriverResponseDTO> findAll() {
    return driverRepository.findAll().stream()
        .map(driverMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public DriverResponseDTO update(Long id, DriverRequestDTO requestDTO) {
    Driver driver = findEntity(id);
    driverMapper.updateEntity(driver, requestDTO);
    return driverMapper.toResponseDTO(driverRepository.save(driver));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    driverRepository.delete(findEntity(id));
  }

  private Driver findEntity(Long id) {
    return driverRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id " + id));
  }
}
