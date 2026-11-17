package com.youcode.covoiturage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.Driver;

/**
 * DriverRepository
 */
public interface DriverRepository extends JpaRepository<Driver, Long> {

}
