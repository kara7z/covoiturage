package com.youcode.covoiturage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.youcode.covoiturage.entity.Reservation;
import com.youcode.covoiturage.enums.ReservationStatus;

/**
 * ReservationRepository
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  boolean existsByPassengerIdAndTripId(Long passengerId, Long tripId);

  Optional<Reservation> findFirstByPassengerIdAndTripId(Long passengerId, Long tripId);

  Page<Reservation> findByPassengerId(Long passengerId, Pageable pageable);

  @Query("""
      select coalesce(sum(r.numberOfSeats), 0) from Reservation r
      where r.trip.id = :tripId
        and r.status = :status
      """)
  Long sumConfirmedSeatsByTripId(
      @Param("tripId") Long tripId, @Param("status") ReservationStatus status);

  default Long sumConfirmedSeatsByTripId(Long tripId) {
    return sumConfirmedSeatsByTripId(tripId, ReservationStatus.CONFIRMED);
  }

  @Query("""
      select count(r) > 0 from Reservation r
      where r.passenger.id = :passengerId
        and r.trip.id = :tripId
        and r.status = :status
      """)
  boolean existsByPassengerIdAndTripIdAndStatus(
      @Param("passengerId") Long passengerId,
      @Param("tripId") Long tripId,
      @Param("status") ReservationStatus status);

  @Query("""
      select r from Reservation r
      join fetch r.trip
      where r.id = :id
      """)
  Optional<Reservation> findByIdFetchTrip(@Param("id") Long id);

  @Query("""
      select r from Reservation r
      where r.trip.id = :tripId
        and r.status = :status
      """)
  List<Reservation> findConfirmedByTripId(
      @Param("tripId") Long tripId, @Param("status") ReservationStatus status);

  default List<Reservation> findConfirmedByTripId(Long tripId) {
    return findConfirmedByTripId(tripId, ReservationStatus.CONFIRMED);
  }

}
