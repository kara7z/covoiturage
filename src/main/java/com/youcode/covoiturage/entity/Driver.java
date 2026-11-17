package com.youcode.covoiturage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Driver
 */
@Entity
@Getter
@Setter
@Table(name = "drivers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver extends User {
  @Column(name = "licenseNumber", nullable = false)
  private String licenseNumber;

  @Builder
  public Driver(String lastName, String firstName, String email,
      String phone, String licenseNumber) {
    super(firstName, lastName, email, phone);
    this.licenseNumber = licenseNumber;
  }

}
