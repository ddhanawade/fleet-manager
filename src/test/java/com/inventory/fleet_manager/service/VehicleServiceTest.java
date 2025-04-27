package com.inventory.fleet_manager.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    public VehicleServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllVehicles() {
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
        when(vehicleRepository.findAll()).thenReturn(mockVehicles);

        // Act
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        // Assert
        assertNotNull(vehicles);
        assertEquals(1, vehicles.size());
        assertEquals("Toyota", vehicles.get(0).getMake());
        verify(vehicleRepository, times(1)).findAll();
    }
}
