package com.youcode.covoiturage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.Driver;

/**
 * DriverRepository
 */
public interface DriverRepository extends JpaRepository<Driver, Long> {

  boolean existsByEmail(String email);

  boolean existsByLicenseNumber(String licenseNumber);

  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);

  List<Driver> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
      String firstName, String lastName, String email);
}
