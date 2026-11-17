package com.youcode.covoiturage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.Passenger;

/**
 * PassengerRepository
 */
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

}
