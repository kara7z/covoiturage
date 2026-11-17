package com.youcode.covoiturage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Vehicle
 */
@Entity
@Table(name = "vehicles")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(nullable = false)
  private String brand;

  @Column(nullable = false)
  private String model;

  @Column(name = "license_plate", nullable = false, unique = true)
  private String licensePlate;

  @Column(nullable = false)
  private String color;

  @Column(name = "passenger_capacity", nullable = false)
  private Integer passengerCapacity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "driver_id", nullable = false)
  private Driver driver;

  @Builder
  public Vehicle(String brand, String model, String licensePlate, String color,
      Integer passengerCapacity, Driver driver) {
    this.brand = brand;
    this.model = model;
    this.licensePlate = licensePlate;
    this.color = color;
    this.passengerCapacity = passengerCapacity;
    this.driver = driver;
  }

}
