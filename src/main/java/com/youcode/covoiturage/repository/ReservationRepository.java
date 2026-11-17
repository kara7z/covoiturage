package com.youcode.covoiturage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youcode.covoiturage.entity.Reservation;

/**
 * ReservationRepository
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
