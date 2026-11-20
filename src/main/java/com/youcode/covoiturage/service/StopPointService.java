package com.youcode.covoiturage.service;

import java.util.List;

import com.youcode.covoiturage.dto.StopPointRequestDTO;
import com.youcode.covoiturage.dto.StopPointResponseDTO;

/**
 * StopPointService
 */
public interface StopPointService {

  StopPointResponseDTO create(StopPointRequestDTO requestDTO);

  StopPointResponseDTO findById(Long id);

  List<StopPointResponseDTO> findAll();

  StopPointResponseDTO update(Long id, StopPointRequestDTO requestDTO);

  void delete(Long id);
}
