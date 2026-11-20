package com.youcode.covoiturage.service;

import java.util.List;

import com.youcode.covoiturage.dto.DriverRequestDTO;
import com.youcode.covoiturage.dto.DriverResponseDTO;

/**
 * DriverService
 */
public interface DriverService {

  DriverResponseDTO create(DriverRequestDTO requestDTO);

  DriverResponseDTO findById(Long id);

  List<DriverResponseDTO> findAll();

  List<DriverResponseDTO> search(String query);

  DriverResponseDTO update(Long id, DriverRequestDTO requestDTO);

  void delete(Long id);
}
