package com.youcode.covoiturage.service;

import java.util.List;

import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;

/**
 * ReservationService
 */
public interface ReservationService {

  ReservationResponseDTO create(ReservationRequestDTO requestDTO);

  ReservationResponseDTO findById(Long id);

  List<ReservationResponseDTO> findAll();

  ReservationResponseDTO update(Long id, ReservationRequestDTO requestDTO);

  void delete(Long id);

  void cancel(Long id, Long passengerId);
}
