package com.youcode.covoiturage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.youcode.covoiturage.dto.ReservationRequestDTO;
import com.youcode.covoiturage.dto.ReservationResponseDTO;
import com.youcode.covoiturage.enums.ReservationStatus;
import com.youcode.covoiturage.exception.GlobalExceptionHandler;
import com.youcode.covoiturage.service.ReservationService;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

  @Mock
  private ReservationService reservationService;

  private MockMvc mockMvc;

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 12, 1, 10, 0);

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new ReservationController(reservationService))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void create_returnsCreatedWithComputedAmount() throws Exception {
    ReservationResponseDTO response = new ReservationResponseDTO(
        10L, 1L, 100L, "Rabat", "Marrakech", 2,
        new BigDecimal("160.00"), NOW, ReservationStatus.CONFIRMED);
    when(reservationService.create(any())).thenReturn(response);

    mockMvc.perform(post("/api/reservations")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"passengerId\":1,\"tripId\":100,\"boardingPoint\":\"Rabat\","
            + "\"dropOffPoint\":\"Marrakech\",\"numberOfSeats\":2,"
            + "\"totalAmount\":\"160.00\",\"reservationDate\":\"2026-12-01T10:00\","
            + "\"status\":\"CONFIRMED\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.totalAmount").value(160.0))
        .andExpect(jsonPath("$.status").value("CONFIRMED"));
  }

  @Test
  void create_returnsBadRequestForValidationFailure() throws Exception {
    mockMvc.perform(post("/api/reservations")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"passengerId\":null,\"tripId\":100}"))
        .andExpect(status().isBadRequest());
  }
}