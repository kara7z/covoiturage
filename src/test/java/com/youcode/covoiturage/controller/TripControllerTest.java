package com.youcode.covoiturage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.youcode.covoiturage.dto.TripDetailResponseDTO;
import com.youcode.covoiturage.dto.TripResponseDTO;
import com.youcode.covoiturage.dto.TripSearchResponseDTO;
import com.youcode.covoiturage.enums.TripStatus;
import com.youcode.covoiturage.exception.BadRequestException;
import com.youcode.covoiturage.exception.BusinessRuleException;
import com.youcode.covoiturage.exception.ForbiddenException;
import com.youcode.covoiturage.exception.GlobalExceptionHandler;
import com.youcode.covoiturage.service.TripService;

@ExtendWith(MockitoExtension.class)
class TripControllerTest {

  @Mock
  private TripService tripService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new TripController(tripService))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void create_returnsCreatedWithPublishedTrip() throws Exception {
    when(tripService.create(any())).thenReturn(new TripResponseDTO(10L, 1L, 2L, "Rabat",
        "Marrakech", LocalDateTime.of(2026, 12, 1, 17, 0), 3, TripStatus.PUBLISHED, List.of()));

    mockMvc.perform(post("/api/trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validPayload()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.departureCity").value("Rabat"))
        .andExpect(jsonPath("$.arrivalCity").value("Marrakech"))
        .andExpect(jsonPath("$.status").value("PUBLISHED"));
  }

  @Test
  void create_returnsBadRequestWhenTripHasFewerThanTwoStops() throws Exception {
    mockMvc.perform(post("/api/trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"driverId":1,"vehicleId":2,"offeredSeats":3,
             "stopPoints":[{"city":"Rabat","passingTime":"2026-12-01T17:00:00",
                            "priceFromDeparture":0}]}
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Validation failed"));
  }

  @Test
  void create_returnsConflictWhenCityAppearsTwice() throws Exception {
    when(tripService.create(any())).thenThrow(new BusinessRuleException("DUPLICATE_STOP_CITY",
        "City Rabat appears more than once in the trip"));

    mockMvc.perform(post("/api/trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validPayload()))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("City Rabat appears more than once in the trip"));
  }

  @Test
  void create_returnsForbiddenWhenVehicleIsNotOwned() throws Exception {
    when(tripService.create(any())).thenThrow(new ForbiddenException("VEHICLE_NOT_OWNED",
        "Vehicle does not belong to driver 1"));

    mockMvc.perform(post("/api/trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validPayload()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.detail").value("Vehicle does not belong to driver 1"));
  }

  private String validPayload() {
    return """
        {"driverId":1,"vehicleId":2,"offeredSeats":3,
         "stopPoints":[
           {"city":"Rabat","passingTime":"2026-12-01T17:00:00","priceFromDeparture":0},
           {"city":"Marrakech","passingTime":"2026-12-01T21:30:00","priceFromDeparture":120}
         ]}
        """;
  }
}
