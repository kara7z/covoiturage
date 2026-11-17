package com.youcode.covoiturage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.Trip;

/**
 * TripRepository
 */
public interface TripRepository extends JpaRepository<Trip, Long> {

}
