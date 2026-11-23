package com.youcode.covoiturage.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.youcode.covoiturage.exception.AlreadyExistsException;
import com.youcode.covoiturage.exception.ForbiddenException;
import com.youcode.covoiturage.exception.GlobalExceptionHandler;
import com.youcode.covoiturage.service.VehicleService;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

  @Mock
  private VehicleService vehicleService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new VehicleController(vehicleService))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void findById_returnsForbiddenWhenVehicleBelongsToAnotherDriver() throws Exception {
    when(vehicleService.findById(2L, 10L))
        .thenThrow(new ForbiddenException("Vehicle does not belong to driver 2"));

    mockMvc.perform(get("/api/drivers/2/vehicles/10"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.detail").value("Vehicle does not belong to driver 2"));
  }

  @Test
  void create_returnsConflictWhenLicensePlateAlreadyExists() throws Exception {
    when(vehicleService.create(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
        .thenThrow(new AlreadyExistsException("Vehicle with license plate ABC-123 already exists"));

    mockMvc.perform(post("/api/drivers/1/vehicles")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"brand":"Toyota","model":"Corolla","licensePlate":"ABC-123",
             "color":"Blue","passengerCapacity":4,"driverId":1}
            """))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Vehicle with license plate ABC-123 already exists"));
  }

  @Test
  void delete_returnsConflictWhenVehicleIsLinkedToTrip() throws Exception {
    doThrow(new AlreadyExistsException("VEHICLE_IN_USE", "Vehicle linked to a trip cannot be deleted"))
        .when(vehicleService).delete(1L, 10L);

    mockMvc.perform(delete("/api/drivers/1/vehicles/10"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Vehicle linked to a trip cannot be deleted"));
  }
}
