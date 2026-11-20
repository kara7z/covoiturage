package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.youcode.covoiturage.config.MapStructConfig;
import com.youcode.covoiturage.dto.DriverRequestDTO;
import com.youcode.covoiturage.dto.DriverResponseDTO;
import com.youcode.covoiturage.entity.Driver;

/**
 * DriverMapper
 */
@Mapper(config = MapStructConfig.class)
public interface DriverMapper {

  @Mapping(target = "fullName", expression = "java(driver.getFirstName() + \" \" + driver.getLastName())")
  DriverResponseDTO toResponseDTO(Driver driver);

  Driver toEntity(DriverRequestDTO requestDTO);

  DriverRequestDTO toRequestDTO(Driver driver);

  void updateEntity(@MappingTarget Driver driver, DriverRequestDTO requestDTO);
}
