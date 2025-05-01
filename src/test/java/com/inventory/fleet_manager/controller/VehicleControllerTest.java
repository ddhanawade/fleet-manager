package com.inventory.fleet_manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @InjectMocks
    private VehicleController vehicleController;

    @Mock
    private VehicleService vehicleService;

    @Test
    public void testGetAllVehicles() {
        // Arrange
        List<VehicleDTO> vehiclesList = new ArrayList<>();
        vehiclesList.add(new VehicleDTO(1L, "Toyota", "Camry", null, null, null, null, null, null, null, null, null, null));
        vehiclesList.add(new VehicleDTO(2L, "Honda", "Civic", null, null, null, null, null, null, null, null, null, null));

        when(vehicleService.getAllVehicles()).thenReturn(vehiclesList);

        // Act
        ResponseEntity<List<VehicleDTO>> response = vehicleController.getAllVehicles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(vehiclesList, response.getBody());
        verify(vehicleService, times(1)).getAllVehicles();
    }

    @Test
    public void testGetVehicleById(){
        // Arrange
      VehicleDTO dto =  new VehicleDTO(1L, "Toyota", "Camry", null, null, null, null, null, null, null, null, null, null);

      when(vehicleService.getVehicleById(1L)).thenReturn(dto);

        // Act
        ResponseEntity<VehicleDTO> response = vehicleController.getVehicleById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(vehicleService, times(1)).getVehicleById(1L);
    }

}