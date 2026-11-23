package com.youcode.covoiturage.service;

import java.util.List;

import com.youcode.covoiturage.dto.VehicleRequestDTO;
import com.youcode.covoiturage.dto.VehicleResponseDTO;

/**
 * VehicleService
 */
public interface VehicleService {

  VehicleResponseDTO create(Long driverId, VehicleRequestDTO requestDTO);

  VehicleResponseDTO findById(Long driverId, Long id);

  List<VehicleResponseDTO> findByDriverId(Long driverId);

  VehicleResponseDTO update(Long driverId, Long id, VehicleRequestDTO requestDTO);

  void delete(Long driverId, Long id);
}
