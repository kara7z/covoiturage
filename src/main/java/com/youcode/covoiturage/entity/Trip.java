package com.youcode.covoiturage.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    this.stopPoints = stopPoints != null ? stopPoints : new ArrayList<>();
  }

  public void addStopPoint(StopPoint stopPoint) {
    stopPoints.add(stopPoint);
    stopPoint.setTrip(this);
  }

  public void removeStopPoint(StopPoint stopPoint) {
    stopPoints.remove(stopPoint);
    stopPoint.setTrip(null);
  }

}
