package com.youcode.covoiturage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.Vehicle;

/**
 * VehicleRepository
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
