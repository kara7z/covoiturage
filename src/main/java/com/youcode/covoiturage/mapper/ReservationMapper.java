package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.entity.Reservation;
import com.youcode.covoiturage.entity.Trip;

/**
 * ReservationMapper
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReservationMapper {

  @Mapping(target = "passenger", source = "passenger")
  @Mapping(target = "trip", source = "trip")
  @Mapping(target = "status", source = "requestDTO.status")
  Reservation toEntity(ReservationRequestDTO requestDTO, Passenger passenger, Trip trip);

  @Mapping(target = "passengerId", source = "passenger.id")
  @Mapping(target = "tripId", source = "trip.id")
  ReservationResponseDTO toResponseDTO(Reservation reservation);

  @Mapping(target = "passenger", source = "passenger")
  @Mapping(target = "trip", source = "trip")
  @Mapping(target = "status", source = "requestDTO.status")
  void updateEntity(@MappingTarget Reservation reservation, ReservationRequestDTO requestDTO,
      Passenger passenger, Trip trip);
}
