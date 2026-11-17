package com.youcode.covoiturage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Passenger
 */
@Entity
@Table(name = "passengers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Passenger extends User {

  @Builder
  public Passenger(String lastName, String firstName, String email, String phone) {
    super(lastName, firstName, email, phone);
  }

}
