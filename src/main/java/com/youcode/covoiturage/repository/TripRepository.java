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
import com.youcode.covoiturage.enums.ReservationStatus;
import com.youcode.covoiturage.enums.TripStatus;

/**
 * TripRepository
 */
public interface TripRepository extends JpaRepository<Trip, Long> {

  Optional<Trip> findFirstByDriver_IdAndDepartureCityAndArrivalCity(
      Long driverId, String departureCity, String arrivalCity);

  boolean existsByVehicleId(Long vehicleId);

  @Query("""
      select t.id as id,
        d.id as driverId,
        concat(concat(d.firstName, ' '), d.lastName) as driverName,
        departure.city as departureCity,
        arrival.city as arrivalCity,
        departure.passingTime as departureTime,
        arrival.passingTime as arrivalTime,
        (arrival.priceFromDeparture - departure.priceFromDeparture) as segmentPrice,
        (t.offeredSeats - coalesce(sum(r.numberOfSeats), 0)) as remainingSeats
      from Trip t
      join t.driver d
      join t.stopPoints departure
      join t.stopPoints arrival
      left join Reservation r on r.trip = t and r.status = :confirmedStatus
      where lower(departure.city) = lower(:departureCity)
        and lower(arrival.city) = lower(:arrivalCity)
        and departure.stopOrder < arrival.stopOrder
        and t.status = :publishedStatus
        and (:dayStart is null or (departure.passingTime >= :dayStart and departure.passingTime < :dayEnd))
        and (:maxPrice is null or (arrival.priceFromDeparture - departure.priceFromDeparture) <= :maxPrice)
      group by t, d, departure, arrival
      having t.offeredSeats > coalesce(sum(r.numberOfSeats), 0)
      order by departure.passingTime asc
      """)
  Page<TripSearchProjection> searchRows(@Param("departureCity") String departureCity,
      @Param("arrivalCity") String arrivalCity,
      @Param("dayStart") LocalDateTime dayStart,
      @Param("dayEnd") LocalDateTime dayEnd,
      @Param("maxPrice") BigDecimal maxPrice,
      Pageable pageable,
      @Param("publishedStatus") TripStatus publishedStatus,
      @Param("confirmedStatus") ReservationStatus confirmedStatus);

  default Page<TripSearchResponseDTO> search(String departureCity, String arrivalCity,
      LocalDateTime dayStart, LocalDateTime dayEnd, BigDecimal maxPrice, Pageable pageable) {
    return searchRows(departureCity, arrivalCity, dayStart, dayEnd, maxPrice, pageable,
        TripStatus.PUBLISHED, ReservationStatus.CONFIRMED)
        .map(row -> new TripSearchResponseDTO(
            row.getId(),
            row.getDriverId(),
            row.getDriverName(),
            row.getDepartureCity(),
            row.getArrivalCity(),
            row.getDepartureTime(),
            row.getArrivalTime(),
            row.getSegmentPrice(),
            row.getRemainingSeats()));
  }

  @Query("""
      select distinct t from Trip t
      join fetch t.driver
      join fetch t.vehicle
      left join fetch t.stopPoints
      where t.id = :id
      """)
  Optional<Trip> findDetailById(@Param("id") Long id);
}
