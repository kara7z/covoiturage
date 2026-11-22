package com.youcode.covoiturage.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.youcode.covoiturage.enums.TripStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Trip
 */
@Entity
@Table(name = "trips")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Trip {

  @Id
  @Setter(AccessLevel.NONE)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "driver_id", nullable = false)
  private Driver driver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vehicle_id", nullable = false)
  private Vehicle vehicle;

  @Column(nullable = false)
  private String departureCity;

  @Column(name = "arrival_city", nullable = false)
  private String arrivalCity;

  @Column(name = "departure_date", nullable = false)
  private LocalDateTime departureDate;

  @Column(name = "offered_seats", nullable = false)
  private Integer offeredSeats;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TripStatus status;

  @Size(min = 2, message = "A trip must have at least two stops")
  @OrderBy("stopOrder ASC")
  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<StopPoint> stopPoints = new ArrayList<>();

  @Builder
  public Trip(Driver driver, Vehicle vehicle, String departureCity, String arrivalCity,
      LocalDateTime departureDate, Integer offeredSeats, TripStatus status,
      List<StopPoint> stopPoints) {
    this.driver = driver;
    this.vehicle = vehicle;
    this.departureCity = departureCity;
    this.arrivalCity = arrivalCity;
    this.departureDate = departureDate;
    this.offeredSeats = offeredSeats;
    this.status = status;
    this.stopPoints = new ArrayList<>();
    if (stopPoints != null) {
      this.stopPoints = stopPoints.stream()
          .sorted(Comparator.comparing(StopPoint::getStopOrder))
          .toList();
      this.stopPoints = new ArrayList<>(this.stopPoints);
      this.stopPoints.forEach(stopPoint -> stopPoint.setTrip(this));
    }
  }

  public void addStopPoint(StopPoint stopPoint) {
    if (stopPoint == null)
      throw new IllegalArgumentException("Stop point cannot be null");

    stopPoint.setTrip(this);
    stopPoints.add(stopPoint);
    stopPoints.sort(Comparator.comparing(StopPoint::getStopOrder,
        Comparator.nullsLast(Integer::compareTo)));
  }

  public void removeStopPoint(StopPoint stopPoint) {
    stopPoints.remove(stopPoint);
    stopPoint.setTrip(null);
  }

  @PrePersist
  @PreUpdate
  private void validateStops() {

    if (stopPoints.size() < 2)
      throw new IllegalStateException("A trip must have at least two stops");

    stopPoints.sort(Comparator.comparing(StopPoint::getStopOrder,
        Comparator.nullsLast(Integer::compareTo)));

    for (int i = 0; i < stopPoints.size(); i++) {
      Integer order = stopPoints.get(i).getStopOrder();
      if (order == null || order != i)
        throw new IllegalStateException("Trip stop orders must start at 0 and be contiguous");

      if (i > 0) {
        StopPoint previous = stopPoints.get(i - 1);
        StopPoint current = stopPoints.get(i);
        if (previous.getPassingTime() != null && current.getPassingTime() != null
            && current.getPassingTime().isBefore(previous.getPassingTime()))
          throw new IllegalStateException("Stop times must follow the stop order");
        if (previous.getPriceFromDeparture() != null && current.getPriceFromDeparture() != null
            && current.getPriceFromDeparture().compareTo(previous.getPriceFromDeparture()) < 0)
          throw new IllegalStateException("Cumulative stop prices must not decrease");
      }
    }
  }

}
