package com.inventory.fleet_manager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
    }

    @Test
    void testGetAllVehicles() throws Exception {
        // Arrange
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setGrade("A");
        vehicle.setFuelType("Hybrid");
        vehicle.setExteriorColor("Black");
        vehicle.setInteriorColor("A");
        vehicle.setLocation("Location A");
        vehicle.setStatus("Available");
        List<Vehicle> mockVehicles = List.of(vehicle);
        when(vehicleService.getAllVehicles()).thenReturn(mockVehicles);
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Camry"));
    }

    @Test
    void testGetVehicleById() throws Exception {
        // Arrange
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setGrade("A");
        vehicle.setFuelType("Hybrid");
        vehicle.setExteriorColor("Black");
        vehicle.setInteriorColor("A");
        vehicle.setLocation("Location A");
        vehicle.setStatus("Available");

        when(vehicleService.getVehicleById(1L)).thenReturn(vehicle);

        // Act & Assert
        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"))
                .andExpect(jsonPath("$.grade").value("A"))
                .andExpect(jsonPath("$.fuelType").value("Hybrid"))
                .andExpect(jsonPath("$.exteriorColor").value("Black"))
                .andExpect(jsonPath("$.interiorColor").value("A"))
                .andExpect(jsonPath("$.location").value("Location A"))
                .andExpect(jsonPath("$.status").value("Available"));
    }

}
