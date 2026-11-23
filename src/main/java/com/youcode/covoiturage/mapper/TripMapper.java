package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.youcode.covoiturage.config.MapStructConfig;
import com.youcode.covoiturage.dto.TripDetailResponseDTO;
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

  @Mapping(target = "driverId", source = "trip.driver.id")
  @Mapping(target = "driverName", source = "trip.driver", qualifiedByName = "driverFullName")
  @Mapping(target = "vehicleId", source = "trip.vehicle.id")
  @Mapping(target = "vehicleBrand", source = "trip.vehicle.brand")
  @Mapping(target = "vehicleModel", source = "trip.vehicle.model")
  @Mapping(target = "remainingSeats", source = "remainingSeats")
  TripDetailResponseDTO toDetailResponseDTO(Trip trip, Long remainingSeats);

  @Named("driverFullName")
  default String driverFullName(Driver driver) {
    return driver == null ? null : driver.getFirstName() + " " + driver.getLastName();
  }

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
