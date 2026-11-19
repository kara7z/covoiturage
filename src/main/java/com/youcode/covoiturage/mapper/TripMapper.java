package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;

/**
 * TripMapper
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TripMapper {

  @Mapping(target = "stopPoints", ignore = true)
  @Mapping(target = "driver", source = "driver")
  @Mapping(target = "vehicle", source = "vehicle")
  Trip toEntity(TripRequestDTO requestDTO, Driver driver, Vehicle vehicle);

  @Mapping(target = "driverId", source = "driver.id")
  @Mapping(target = "vehicleId", source = "vehicle.id")
  TripResponseDTO toResponseDTO(Trip trip);

  @Mapping(target = "stopPoints", ignore = true)
  @Mapping(target = "driver", source = "driver")
  @Mapping(target = "vehicle", source = "vehicle")
  void updateEntity(@MappingTarget Trip trip, TripRequestDTO requestDTO, Driver driver, Vehicle vehicle);
}
