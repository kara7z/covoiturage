package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.youcode.covoiturage.config.MapStructConfig;
import com.youcode.covoiturage.dto.VehicleRequestDTO;
import com.youcode.covoiturage.dto.VehicleResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Vehicle;

/**
 * VehicleMapper
 */
@Mapper(config = MapStructConfig.class)
public interface VehicleMapper {

  @Mapping(target = "driver", source = "driver")
  Vehicle toEntity(VehicleRequestDTO requestDTO, Driver driver);

  @Mapping(target = "driverId", source = "driver.id")
  VehicleResponseDTO toResponseDTO(Vehicle vehicle);

  @Mapping(target = "driver", source = "driver")
  void updateEntity(@MappingTarget Vehicle vehicle, VehicleRequestDTO requestDTO, Driver driver);
}
