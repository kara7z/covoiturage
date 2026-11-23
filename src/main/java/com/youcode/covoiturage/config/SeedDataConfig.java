package com.youcode.covoiturage.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.TripRequestDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.dto.TripStopRequestDTO;
import com.youcode.covoiturage.entity.Driver;
import com.youcode.covoiturage.entity.Passenger;
import com.youcode.covoiturage.entity.Trip;
import com.youcode.covoiturage.entity.Vehicle;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.repository.PassengerRepository;
import com.youcode.covoiturage.repository.ReservationRepository;
import com.youcode.covoiturage.repository.TripRepository;
import com.youcode.covoiturage.repository.VehicleRepository;
import com.youcode.covoiturage.service.ReservationService;
import com.youcode.covoiturage.service.TripService;

import lombok.extern.slf4j.Slf4j;

/**
 * Seeds a repeatable demonstration dataset for local development.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class SeedDataConfig {

  private static final List<DriverSeed> DRIVERS = List.of(
      new DriverSeed("Hamid", "El Amrani", "hamid@example.com", "0600000001", "AB-123456"),
      new DriverSeed("Youssef", "Benali", "youssef@example.com", "0600000002", "AB-123457"),
      new DriverSeed("Salma", "Idrissi", "salma@example.com", "0600000003", "AB-123458"),
      new DriverSeed("Amine", "Alaoui", "amine@example.com", "0600000004", "AB-123459"),
      new DriverSeed("Nadia", "Berrada", "nadia@example.com", "0600000005", "AB-123460"));

  private static final List<PassengerSeed> PASSENGERS = List.of(
      new PassengerSeed("Sara", "Amrani", "sara@example.com", "0610000001"),
      new PassengerSeed("Yassine", "Tazi", "yassine@example.com", "0610000002"),
      new PassengerSeed("Lina", "Zahra", "lina@example.com", "0610000003"),
      new PassengerSeed("Omar", "Belkadi", "omar@example.com", "0610000004"),
      new PassengerSeed("Aya", "Bennani", "aya@example.com", "0610000005"));

  private static final List<VehicleSeed> VEHICLES = List.of(
      new VehicleSeed("Dacia", "Logan", "12345-A-6", "White", 4),
      new VehicleSeed("Renault", "Clio", "23456-B-6", "Blue", 4),
      new VehicleSeed("Peugeot", "208", "34567-C-6", "Grey", 4),
      new VehicleSeed("Hyundai", "i20", "45678-D-6", "Black", 4),
      new VehicleSeed("Toyota", "Yaris", "56789-E-6", "Red", 4));

  private static final List<TripSeed> TRIPS = List.of(
      new TripSeed(List.of(
          new StopSeed("Rabat", 0, "0.00"),
          new StopSeed("Casablanca", 75, "40.00"),
          new StopSeed("Settat", 135, "70.00"),
          new StopSeed("Marrakech", 270, "120.00"))),
      new TripSeed(List.of(
          new StopSeed("Casablanca", 0, "0.00"),
          new StopSeed("Agadir", 300, "180.00"))),
      new TripSeed(List.of(
          new StopSeed("Fes", 0, "0.00"),
          new StopSeed("Meknes", 60, "35.00"),
          new StopSeed("Casablanca", 240, "140.00"))),
      new TripSeed(List.of(
          new StopSeed("Tangier", 0, "0.00"),
          new StopSeed("Rabat", 210, "125.00"))),
      new TripSeed(List.of(
          new StopSeed("Marrakech", 0, "0.00"),
          new StopSeed("Ouarzazate", 150, "100.00"))));

  @Bean
  CommandLineRunner seedDemoData(DriverRepository driverRepository,
      PassengerRepository passengerRepository,
      VehicleRepository vehicleRepository,
      TripRepository tripRepository,
      ReservationRepository reservationRepository,
      TripService tripService,
      ReservationService reservationService) {
    return args -> {
      List<Driver> drivers = seedDrivers(driverRepository);
      List<Passenger> passengers = seedPassengers(passengerRepository);
      List<Vehicle> vehicles = seedVehicles(vehicleRepository, drivers);
      List<Trip> trips = seedTrips(tripRepository, tripService, drivers, vehicles);
      seedReservations(reservationRepository, reservationService, passengers, trips);

      log.info("Seeded demo data: {} drivers, {} passengers, {} vehicles, {} trips, {} reservations",
          driverRepository.count(), passengerRepository.count(), vehicleRepository.count(),
          tripRepository.count(), reservationRepository.count());
    };
  }

  private List<Driver> seedDrivers(DriverRepository repository) {
    return DRIVERS.stream()
        .map(seed -> repository.findByEmail(seed.email()).orElseGet(() -> repository.save(
            Driver.builder()
                .firstName(seed.firstName())
                .lastName(seed.lastName())
                .email(seed.email())
                .phone(seed.phone())
                .licenseNumber(seed.licenseNumber())
                .build())))
        .toList();
  }

  private List<Passenger> seedPassengers(PassengerRepository repository) {
    return PASSENGERS.stream()
        .map(seed -> repository.findByEmail(seed.email()).orElseGet(() -> repository.save(
            Passenger.builder()
                .firstName(seed.firstName())
                .lastName(seed.lastName())
                .email(seed.email())
                .phone(seed.phone())
                .build())))
        .toList();
  }

  private List<Vehicle> seedVehicles(VehicleRepository repository, List<Driver> drivers) {
    return VEHICLES.stream()
        .map(seed -> repository.findByLicensePlate(seed.licensePlate()).orElseGet(() -> {
          int index = VEHICLES.indexOf(seed);
          return repository.save(Vehicle.builder()
              .brand(seed.brand())
              .model(seed.model())
              .licensePlate(seed.licensePlate())
              .color(seed.color())
              .passengerCapacity(seed.passengerCapacity())
              .driver(drivers.get(index))
              .build());
        }))
        .toList();
  }

  private List<Trip> seedTrips(TripRepository repository, TripService tripService,
      List<Driver> drivers, List<Vehicle> vehicles) {
    LocalDate firstDepartureDay = LocalDate.now().plusDays(1);
    return TRIPS.stream()
        .map(seed -> {
          int index = TRIPS.indexOf(seed);
          List<StopSeed> stops = seed.stops();
          String departureCity = stops.get(0).city();
          String arrivalCity = stops.get(stops.size() - 1).city();
          Driver driver = drivers.get(index);
          return repository.findFirstByDriver_IdAndDepartureCityAndArrivalCity(
              driver.getId(), departureCity, arrivalCity)
              .orElseGet(() -> createTrip(tripService, driver, vehicles.get(index),
                  firstDepartureDay.plusDays(index), stops, repository));
        })
        .toList();
  }

  private Trip createTrip(TripService tripService, Driver driver, Vehicle vehicle,
      LocalDate departureDay, List<StopSeed> stops, TripRepository tripRepository) {
    List<TripStopRequestDTO> requestStops = stops.stream()
        .map(stop -> new TripStopRequestDTO(
            stop.city(),
            departureDay.atTime(17, 0).plusMinutes(stop.offsetMinutes()),
            new BigDecimal(stop.price())))
        .toList();
    TripResponseDTO response = tripService.create(new TripRequestDTO(
        driver.getId(), vehicle.getId(), 3, requestStops));
    log.info("Seeded trip {} for driver {} ({} -> {})",
        response.id(), driver.getEmail(), response.departureCity(), response.arrivalCity());
    return tripRepository.findById(response.id())
        .orElseThrow(() -> new IllegalStateException("Seeded trip not found: " + response.id()));
  }

  private void seedReservations(ReservationRepository repository,
      ReservationService reservationService, List<Passenger> passengers, List<Trip> trips) {
    List<BookingSeed> bookings = List.of(
        new BookingSeed(0, 0, "Rabat", "Casablanca"),
        new BookingSeed(1, 1, "Casablanca", "Agadir"),
        new BookingSeed(2, 2, "Meknes", "Casablanca"),
        new BookingSeed(3, 3, "Tangier", "Rabat"),
        new BookingSeed(4, 4, "Marrakech", "Ouarzazate"));

    for (BookingSeed booking : bookings) {
      Passenger passenger = passengers.get(booking.passengerIndex());
      Trip trip = trips.get(booking.tripIndex());
      if (!repository.existsByPassengerIdAndTripId(passenger.getId(), trip.getId())) {
        reservationService.create(new ReservationRequestDTO(
            passenger.getId(), trip.getId(), booking.boardingPoint(), booking.dropOffPoint(),
            1, null, null, null));
      }
    }
  }

  private record DriverSeed(String firstName, String lastName, String email,
      String phone, String licenseNumber) {
  }

  private record PassengerSeed(String firstName, String lastName, String email, String phone) {
  }

  private record VehicleSeed(String brand, String model, String licensePlate,
      String color, Integer passengerCapacity) {
  }

  private record TripSeed(List<StopSeed> stops) {
  }

  private record StopSeed(String city, int offsetMinutes, String price) {
  }

  private record BookingSeed(int passengerIndex, int tripIndex,
      String boardingPoint, String dropOffPoint) {
  }
}
