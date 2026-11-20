package com.youcode.covoiturage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.youcode.covoiturage.entity.Driver;

/**
 * DriverRepository
 */
public interface DriverRepository extends JpaRepository<Driver, Long> {

  boolean existsByEmail(String email);

  boolean existsByLicenseNumber(String licenseNumber);

  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);

  @Query("""
      select d from Driver d
      where lower(d.firstName) like lower(concat('%', :term, '%'))
         or lower(d.lastName) like lower(concat('%', :term, '%'))
         or lower(d.email) like lower(concat('%', :term, '%'))
      """)
  List<Driver> searchByNameOrEmail(@Param("term") String term);
}
