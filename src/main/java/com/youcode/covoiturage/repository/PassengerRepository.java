package com.youcode.covoiturage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.Passenger;

/**
 * PassengerRepository
 */
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  Optional<Passenger> findByEmail(String email);

}
