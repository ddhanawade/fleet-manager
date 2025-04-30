package com.inventory.fleet_manager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.inventory.fleet_manager.dto.VehicleDTO;
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
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(1L);
        vehicleDTO.setMake("Toyota");
        vehicleDTO.setModel("Camry");
        vehicleDTO.setGrade("A");
        vehicleDTO.setFuelType("Hybrid");
        vehicleDTO.setExteriorColor("Black");
        vehicleDTO.setInteriorColor("A");
        vehicleDTO.setLocation("Location A");
        vehicleDTO.setStatus("Available");
        List<VehicleDTO> mockVehicles = List.of(vehicleDTO);

        when(vehicleService.getAllVehicles()).thenReturn(mockVehicles);

        // Act & Assert
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Camry"));
    }

    @Test
    void testGetVehicleById() throws Exception {

    }

}
