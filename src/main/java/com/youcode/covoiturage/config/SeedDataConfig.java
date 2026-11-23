package com.youcode.covoiturage.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.dto.TripStopRequestDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.repository.VehicleRepository;
import com.youcode.covoiturage.service.TripService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeds the demonstration dataset (Hamid's Rabat -> Marrakech trip).
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class SeedDataConfig {

  @Bean
  CommandLineRunner seedHamidTrip(DriverRepository driverRepository,
      VehicleRepository vehicleRepository, TripService tripService) {
    return args -> {
      if (driverRepository.count() > 0) {
        log.info("Seed data already present, skipping");
        return;
      }

      Driver hamid = driverRepository.save(Driver.builder()
          .firstName("Hamid")
          .lastName("El Amrani")
          .email("hamid@example.com")
          .phone("0600000001")
          .licenseNumber("AB-123456")
          .build());

      Vehicle logan = vehicleRepository.save(Vehicle.builder()
          .brand("Dacia")
          .model("Logan")
          .licensePlate("12345-A-6")
          .color("White")
          .passengerCapacity(4)
          .driver(hamid)
          .build());

      LocalDate departureDay = LocalDate.now().plusDays(1);
      TripResponseDTO trip = tripService.create(new TripRequestDTO(
          hamid.getId(),
          logan.getId(),
          3,
          List.of(
              new TripStopRequestDTO("Rabat", departureDay.atTime(17, 0), BigDecimal.ZERO),
              new TripStopRequestDTO("Casablanca", departureDay.atTime(18, 15), new BigDecimal("40.00")),
              new TripStopRequestDTO("Settat", departureDay.atTime(19, 15), new BigDecimal("70.00")),
              new TripStopRequestDTO("Marrakech", departureDay.atTime(21, 30), new BigDecimal("120.00")))));

      log.info("Seeded Hamid's trip {} ({} -> {})", trip.id(), trip.departureCity(), trip.arrivalCity());
    };
  }
}
