package com.youcode.covoiturage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.youcode.covoiturage.enums.ReservationStatus;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reservation
 */
@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Reservation {

  @Id
  @Setter(AccessLevel.NONE)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "passenger_id", nullable = false)
  private Passenger passenger;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @Column(name = "boarding_point", nullable = false)
  private String boardingPoint;

  @Column(name = "drop_off_point", nullable = false)
  private String dropOffPoint;

  @Column(name = "number_of_seats", nullable = false)
  private Integer numberOfSeats;

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "reservation_date", nullable = false)
  private LocalDateTime reservationDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReservationStatus status;

  @Builder
  public Reservation(Passenger passenger, Trip trip, String boardingPoint, String dropOffPoint,
      Integer numberOfSeats, BigDecimal totalAmount, LocalDateTime reservationDate,
      ReservationStatus status) {
    this.passenger = passenger;
    this.trip = trip;
    this.boardingPoint = boardingPoint;
    this.dropOffPoint = dropOffPoint;
    this.numberOfSeats = numberOfSeats;
    this.totalAmount = totalAmount;
    this.reservationDate = reservationDate;
    this.status = status;
  }

}
