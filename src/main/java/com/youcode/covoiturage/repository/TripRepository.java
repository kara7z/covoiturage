package com.youcode.covoiturage.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.youcode.covoiturage.dto.TripSearchResponseDTO;
import com.youcode.covoiturage.entity.Trip;

/**
 * TripRepository
 */
public interface TripRepository extends JpaRepository<Trip, Long> {

  boolean existsByVehicleId(Long vehicleId);

  @Query("""
      select new com.youcode.covoiturage.dto.TripSearchResponseDTO(
        t.id,
        d.id,
        concat(concat(d.firstName, ' '), d.lastName),
        departure.city,
        arrival.city,
        departure.passingTime,
        arrival.passingTime,
        (arrival.priceFromDeparture - departure.priceFromDeparture),
        (t.offeredSeats - coalesce(
          (select sum(r.numberOfSeats) from Reservation r
           where r.trip = t
             and r.status = com.youcode.covoiturage.enums.ReservationStatus.CONFIRMED), 0))
      )
      from Trip t
      join t.driver d
      join t.stopPoints departure
      join t.stopPoints arrival
      where lower(departure.city) = lower(:departureCity)
        and lower(arrival.city) = lower(:arrivalCity)
        and departure.stopOrder < arrival.stopOrder
        and t.status = com.youcode.covoiturage.enums.TripStatus.PUBLISHED
        and (:dayStart is null or (departure.passingTime >= :dayStart and departure.passingTime < :dayEnd))
        and (:maxPrice is null or (arrival.priceFromDeparture - departure.priceFromDeparture) <= :maxPrice)
        and (t.offeredSeats - coalesce(
          (select sum(r.numberOfSeats) from Reservation r
           where r.trip = t
             and r.status = com.youcode.covoiturage.enums.ReservationStatus.CONFIRMED), 0)) > 0
      order by departure.passingTime asc
      """)
  Page<TripSearchResponseDTO> search(@Param("departureCity") String departureCity,
      @Param("arrivalCity") String arrivalCity,
      @Param("dayStart") LocalDateTime dayStart,
      @Param("dayEnd") LocalDateTime dayEnd,
      @Param("maxPrice") BigDecimal maxPrice,
      Pageable pageable);

  @Query("""
      select distinct t from Trip t
      join fetch t.driver
      join fetch t.vehicle
      left join fetch t.stopPoints
      where t.id = :id
      """)
  Optional<Trip> findDetailById(@Param("id") Long id);
}
