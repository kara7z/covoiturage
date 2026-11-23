package com.youcode.covoiturage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.Vehicle;

/**
 * VehicleRepository
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

  Optional<Vehicle> findByLicensePlate(String licensePlate);

  boolean existsByLicensePlate(String licensePlate);

  boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);

  List<Vehicle> findByDriverId(Long driverId);

}
