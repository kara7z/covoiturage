package com.youcode.covoiturage.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.youcode.covoiturage.dto.DriverRequestDTO;
import com.youcode.covoiturage.dto.DriverResponseDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.exception.AlreadyExistsException;
import com.youcode.covoiturage.exception.ResourceNotFoundException;
import com.youcode.covoiturage.mapper.DriverMapper;
import com.youcode.covoiturage.repository.DriverRepository;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

  @Mock
  private DriverRepository driverRepository;

  @Mock
  private DriverMapper driverMapper;

  @InjectMocks
  private DriverServiceImpl driverService;

  private DriverRequestDTO request;
  private Driver driver;
  private DriverResponseDTO response;

  @BeforeEach
  void setUp() {
    request = new DriverRequestDTO("Hamid", "El Amrani", "hamid@example.com", "0600000000", "LIC-001");
    driver = Driver.builder()
        .firstName("Hamid")
        .lastName("El Amrani")
        .email("hamid@example.com")
        .phone("0600000000")
        .licenseNumber("LIC-001")
        .build();
    response = new DriverResponseDTO(1L, "Hamid El Amrani", "hamid@example.com", "0600000000", "LIC-001");
  }

  @Test
  void create_returnsCreatedDriver() {
    when(driverRepository.existsByEmail(request.email())).thenReturn(false);
    when(driverRepository.existsByLicenseNumber(request.licenseNumber())).thenReturn(false);
    when(driverMapper.toEntity(request)).thenReturn(driver);
    when(driverRepository.save(driver)).thenReturn(driver);
    when(driverMapper.toResponseDTO(driver)).thenReturn(response);

    DriverResponseDTO result = driverService.create(request);

    assertThat(result).isEqualTo(response);
    verify(driverRepository).save(driver);
  }

  @Test
  void create_throwsWhenEmailAlreadyExists() {
    when(driverRepository.existsByEmail(request.email())).thenReturn(true);

    assertThatThrownBy(() -> driverService.create(request))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessageContaining("email");

    verify(driverRepository, never()).save(any());
  }

  @Test
  void create_throwsWhenLicenseNumberAlreadyExists() {
    when(driverRepository.existsByEmail(request.email())).thenReturn(false);
    when(driverRepository.existsByLicenseNumber(request.licenseNumber())).thenReturn(true);

    assertThatThrownBy(() -> driverService.create(request))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessageContaining("license number");

    verify(driverRepository, never()).save(any());
  }

  @Test
  void findById_throwsWhenDriverDoesNotExist() {
    when(driverRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> driverService.findById(99L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void update_throwsWhenEmailBelongsToAnotherDriver() {
    when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
    when(driverRepository.existsByEmailAndIdNot(request.email(), 1L)).thenReturn(true);

    assertThatThrownBy(() -> driverService.update(1L, request))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessageContaining("email");

    verify(driverRepository, never()).save(any());
  }

  @Test
  void search_returnsMatchingDrivers() {
    when(driverRepository
        .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            "hamid", "hamid", "hamid"))
        .thenReturn(List.of(driver));
    when(driverMapper.toResponseDTO(driver)).thenReturn(response);

    List<DriverResponseDTO> result = driverService.search("  hamid  ");

    assertThat(result).containsExactly(response);
  }
}
