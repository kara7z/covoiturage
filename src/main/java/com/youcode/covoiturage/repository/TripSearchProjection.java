package com.youcode.covoiturage.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TripSearchProjection {

  Long getId();

  Long getDriverId();

  String getDriverName();

  String getDepartureCity();

  String getArrivalCity();

  LocalDateTime getDepartureTime();

  LocalDateTime getArrivalTime();

  BigDecimal getSegmentPrice();

  Long getRemainingSeats();
}
