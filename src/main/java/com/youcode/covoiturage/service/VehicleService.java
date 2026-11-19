package com.youcode.covoiturage.service;

import java.util.List;

import com.youcode.covoiturage.dto.VehicleRequestDTO;
import com.youcode.covoiturage.dto.VehicleResponseDTO;

/**
 * VehicleService
 */
public interface VehicleService {

  VehicleResponseDTO create(VehicleRequestDTO requestDTO);

  VehicleResponseDTO findById(Long id);

  List<VehicleResponseDTO> findAll();

  VehicleResponseDTO update(Long id, VehicleRequestDTO requestDTO);

  void delete(Long id);
}
