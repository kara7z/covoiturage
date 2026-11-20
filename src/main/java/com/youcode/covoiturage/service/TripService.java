package com.youcode.covoiturage.service;

import java.util.List;

import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;

/**
 * TripService
 */
public interface TripService {

  TripResponseDTO create(TripRequestDTO requestDTO);

  TripResponseDTO findById(Long id);

  List<TripResponseDTO> findAll();

  TripResponseDTO update(Long id, TripRequestDTO requestDTO);

  void delete(Long id);
}
