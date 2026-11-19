package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.youcode.covoiturage.dto.PassangerRequestDTO;
import com.youcode.covoiturage.dto.PassangerResponseDTO;
import com.youcode.covoiturage.entity.Passenger;

/**
 * PassengerMapper
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PassengerMapper {

  Passenger toEntity(PassangerRequestDTO requestDTO);

  PassangerRequestDTO toRequestDTO(Passenger passenger);

  @Mapping(target = "fullName", expression = "java(passenger.getFirstName() + \" \" + passenger.getLastName())")
  PassangerResponseDTO toResponseDTO(Passenger passenger);

  void updateEntity(@MappingTarget Passenger passenger, PassangerRequestDTO requestDTO);
}
