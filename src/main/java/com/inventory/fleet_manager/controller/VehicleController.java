package com.inventory.fleet_manager.controller;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
//@CrossOrigin(origins = "http://fleet-manager-client.s3-website.us-east-2.amazonaws.com")
@CrossOrigin(origins = "http://localhost:4200")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) throws VehicleNotFoundException {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleService.createVehicle(convertToEntity(vehicleDTO));
        return ResponseEntity.ok(convertToDTO(vehicle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) throws VehicleNotFoundException {
        Vehicle updatedVehicle = vehicleService.updateVehicle(id, convertToEntity(vehicleDTO));
        return ResponseEntity.ok(convertToDTO(updatedVehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) throws VehicleNotFoundException {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setGrade(vehicle.getGrade());
        dto.setFuelType(vehicle.getFuelType());
        dto.setExteriorColor(vehicle.getExteriorColor());
        dto.setInteriorColor(vehicle.getInteriorColor());
        dto.setLocation(vehicle.getLocation());
        dto.setStatus(vehicle.getStatus());
        dto.setChassisNumber(vehicle.getChassisNumber());
        dto.setEngineNumber(vehicle.getEngineNumber());
        dto.setKeyNumber(vehicle.getKeyNumber());
        dto.setReceivedDate(vehicle.getReceivedDate());
        return dto;
    }

    private Vehicle convertToEntity(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(dto.getMake());
        vehicle.setModel(dto.getModel());
        vehicle.setGrade(dto.getGrade());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setExteriorColor(dto.getExteriorColor());
        vehicle.setInteriorColor(dto.getInteriorColor());
        vehicle.setLocation(dto.getLocation());
        vehicle.setStatus(dto.getStatus());
        vehicle.setChassisNumber(dto.getChassisNumber());
        vehicle.setEngineNumber(dto.getEngineNumber());
        vehicle.setKeyNumber(dto.getKeyNumber());
        vehicle.setReceivedDate(dto.getReceivedDate());
        return vehicle;
    }
}
