package com.youcode.covoiturage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * StopPoint
 */
@Entity
@Table(name = "stop_points", uniqueConstraints = {
    @UniqueConstraint(name = "uk_stop_point_trip_city", columnNames = { "trip_id", "city" }),
    @UniqueConstraint(name = "uk_stop_point_trip_order", columnNames = { "trip_id", "stop_order" })
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class StopPoint {
  @Id
  @Setter(AccessLevel.NONE)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @Column(nullable = false, length = 100)
  private String city;

  @Column(name = "stop_order", nullable = false)
  private Integer stopOrder;

  @Column(name = "passing_time", nullable = false)
  private LocalDateTime passingTime;

  @Column(name = "price_from_departure", nullable = false, precision = 10, scale = 2)
  private BigDecimal priceFromDeparture;

  @Builder
  public StopPoint(Trip trip, String city, Integer stopOrder, LocalDateTime passingTime,
      BigDecimal priceFromDeparture) {
    this.trip = trip;
    this.city = city;
    this.stopOrder = stopOrder;
    this.passingTime = passingTime;
    this.priceFromDeparture = priceFromDeparture;
  }

}
