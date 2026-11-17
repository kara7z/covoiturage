package com.youcode.covoiturage.entity;

import java.time.LocalDateTime;

import com.youcode.covoiturage.enums.ReservationStatus;

/**
 * Reservation
 */
public class Reservation {

  private Long id;
  private Passenger passenger;
  private Trip trip;
  private String boardingPoint;
  private Integer numberOfSteats;
  private Double totaAmount;
  private LocalDateTime reservationDate;
  private ReservationStatus status;

}
