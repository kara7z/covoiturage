package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.youcode.covoiturage.config.MapStructConfig;
import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;

/**
 * TripMapper
 */
@Mapper(config = MapStructConfig.class, uses = StopPointMapper.class)
public interface TripMapper {

  @Mapping(target = "driver", source = "driver")
  @Mapping(target = "vehicle", source = "vehicle")
  @Mapping(target = "departureCity", ignore = true)
  @Mapping(target = "arrivalCity", ignore = true)
  @Mapping(target = "departureDate", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "stopPoints", ignore = true)
  Trip toEntity(TripRequestDTO requestDTO, Driver driver, Vehicle vehicle);

  @Mapping(target = "driverId", source = "driver.id")
  @Mapping(target = "vehicleId", source = "vehicle.id")
  TripResponseDTO toResponseDTO(Trip trip);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "driver", source = "driver")
  @Mapping(target = "vehicle", source = "vehicle")
  @Mapping(target = "departureCity", ignore = true)
  @Mapping(target = "arrivalCity", ignore = true)
  @Mapping(target = "departureDate", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "stopPoints", ignore = true)
  void updateEntity(@MappingTarget Trip trip, TripRequestDTO requestDTO, Driver driver, Vehicle vehicle);
}
