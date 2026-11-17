package com.youcode.covoiturage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.youcode.covoiturage.dto.DriverRequestDTO;
import com.youcode.covoiturage.dto.DriverResponseDTO;
import com.youcode.covoiturage.entity.Driver;

/**
 * Converts Driver DTOs to/from the JPA entity.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {

  Driver toEntity(DriverRequestDTO request);

  DriverResponseDTO toResponse(Driver driver);
}
