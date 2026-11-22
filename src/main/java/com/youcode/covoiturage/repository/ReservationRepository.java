package com.youcode.covoiturage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.youcode.covoiturage.entity.Reservation;

/**
 * ReservationRepository
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Page<Reservation> findByPassengerId(Long passengerId, Pageable pageable);

  @Query("""
      select coalesce(sum(r.numberOfSeats), 0) from Reservation r
      where r.trip.id = :tripId
        and r.status = com.youcode.covoiturage.enums.ReservationStatus.CONFIRMED
      """)
  Long sumConfirmedSeatsByTripId(@Param("tripId") Long tripId);

}
