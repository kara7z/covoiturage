package com.youcode.covoiturage.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.youcode.covoiturage.dto.PassangerRequestDTO;
import com.youcode.covoiturage.dto.PassangerResponseDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;

/**
 * PassengerService
 */
public interface PassengerService {

  PassangerResponseDTO create(PassangerRequestDTO requestDTO);

  PassangerResponseDTO findById(Long id);

  List<PassangerResponseDTO> findAll();

  PassangerResponseDTO findByEmail(String email);

  Page<ReservationResponseDTO> findReservationHistory(Long passengerId, Pageable pageable);

  PassangerResponseDTO update(Long id, PassangerRequestDTO requestDTO);

  void delete(Long id);
}
