package com.youcode.covoiturage.service;

import java.util.List;

import com.youcode.covoiturage.dto.PassangerRequestDTO;
import com.youcode.covoiturage.dto.PassangerResponseDTO;

/**
 * PassengerService
 */
public interface PassengerService {

  PassangerResponseDTO create(PassangerRequestDTO requestDTO);

  PassangerResponseDTO findById(Long id);

  List<PassangerResponseDTO> findAll();

  PassangerResponseDTO findByEmail(String email);

  PassangerResponseDTO update(Long id, PassangerRequestDTO requestDTO);

  void delete(Long id);
}
