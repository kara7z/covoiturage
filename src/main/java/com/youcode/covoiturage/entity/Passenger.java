package com.youcode.covoiturage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Passenger
 */
@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Passenger extends User {

  @Builder
  public Passenger(String firstName, String lastName, String email, String phone) {
    super(firstName, lastName, email, phone);
  }

}
