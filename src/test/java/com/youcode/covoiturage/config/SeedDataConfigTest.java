package com.youcode.covoiturage.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.youcode.covoiturage.enums.ReservationStatus;
import com.youcode.covoiturage.repository.DriverRepository;
import com.youcode.covoiturage.repository.PassengerRepository;
import com.youcode.covoiturage.repository.ReservationRepository;
import com.youcode.covoiturage.repository.StopPointRepository;
import com.youcode.covoiturage.repository.TripRepository;
import com.youcode.covoiturage.repository.VehicleRepository;

@SpringBootTest(properties = "app.seed.enabled=true")
class SeedDataConfigTest {

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private PassengerRepository passengerRepository;

  @Autowired
  private VehicleRepository vehicleRepository;

  @Autowired
  private TripRepository tripRepository;

  @Autowired
  private StopPointRepository stopPointRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  @Qualifier("seedDemoData")
  private CommandLineRunner seedDemoData;

  @Test
  void seedsAtLeastFiveRowsPerTableAndHamidFareScenarioIdempotently() throws Exception {
    assertThat(driverRepository.count()).isGreaterThanOrEqualTo(5);
    assertThat(passengerRepository.count()).isGreaterThanOrEqualTo(5);
    assertThat(vehicleRepository.count()).isGreaterThanOrEqualTo(5);
    assertThat(tripRepository.count()).isGreaterThanOrEqualTo(5);
    assertThat(stopPointRepository.count()).isGreaterThanOrEqualTo(5);
    assertThat(reservationRepository.count()).isGreaterThanOrEqualTo(5);

    var hamid = driverRepository.findByEmail("hamid@example.com").orElseThrow();
    var trip = tripRepository.findFirstByDriver_IdAndDepartureCityAndArrivalCity(
        hamid.getId(), "Rabat", "Marrakech").orElseThrow();
    var sara = passengerRepository.findByEmail("sara@example.com").orElseThrow();
    var reservation = reservationRepository.findFirstByPassengerIdAndTripId(
        sara.getId(), trip.getId()).orElseThrow();

    assertThat(reservation.getBoardingPoint()).isEqualTo("Rabat");
    assertThat(reservation.getDropOffPoint()).isEqualTo("Casablanca");
    assertThat(reservation.getTotalAmount()).isEqualByComparingTo("40.00");
    assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

    long tripCount = tripRepository.count();
    long reservationCount = reservationRepository.count();
    seedDemoData.run();
    assertThat(tripRepository.count()).isEqualTo(tripCount);
    assertThat(reservationRepository.count()).isEqualTo(reservationCount);
  }
}
