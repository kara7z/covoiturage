package com.youcode.covoiturage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.youcode.covoiturage.entity.Reservation;

/**
 * ReservationRepository
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Page<Reservation> findByPassengerId(Long passengerId, Pageable pageable);

}
