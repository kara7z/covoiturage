package com.youcode.covoiturage.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.youcode.covoiturage.dto.TripDetailResponseDTO;
import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.dto.TripSearchResponseDTO;

/**
 * TripService
 */
public interface TripService {

  TripResponseDTO create(TripRequestDTO requestDTO);

  TripResponseDTO findById(Long id);

  TripDetailResponseDTO findDetail(Long id);

  List<TripResponseDTO> findAll();

  Page<TripSearchResponseDTO> search(String departureCity, String arrivalCity,
      LocalDate date, BigDecimal maxPrice, Pageable pageable);

  TripResponseDTO update(Long id, TripRequestDTO requestDTO);

  TripResponseDTO update(Long id, Long driverId, TripRequestDTO requestDTO);

  void delete(Long id);

  void delete(Long id, Long driverId);

  void cancel(Long id, Long driverId);

  void complete(Long id, Long driverId);
}
