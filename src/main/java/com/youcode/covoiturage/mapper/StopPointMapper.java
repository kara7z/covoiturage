package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.youcode.covoiturage.dto.StopPointRequestDTO;
import com.youcode.covoiturage.dto.StopPointResponseDTO;
import com.youcode.covoiturage.entity.StopPoint;
import com.youcode.covoiturage.entity.Trip;

/**
 * StopPointMapper
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StopPointMapper {

  @Mapping(target = "trip", source = "trip")
  StopPoint toEntity(StopPointRequestDTO requestDTO, Trip trip);

  @Mapping(target = "tripId", source = "trip.id")
  StopPointResponseDTO toResponseDTO(StopPoint stopPoint);

  @Mapping(target = "trip", source = "trip")
  void updateEntity(@MappingTarget StopPoint stopPoint, StopPointRequestDTO requestDTO, Trip trip);
}
