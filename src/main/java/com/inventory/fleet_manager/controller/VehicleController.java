package com.inventory.fleet_manager.controller;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.mapper.VehicleMapper;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
//@CrossOrigin(origins = "http://fleet-manager-client.s3-website.us-east-2.amazonaws.com")
@CrossOrigin(origins = "http://localhost:4200")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService, VehicleMapper vehicleMapper) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        List<VehicleDTO> vehiclesList = vehicleService.getAllVehicles();
        return new ResponseEntity<>(vehiclesList, HttpStatus.OK);
    }

    @GetMapping("/getUniqueVehicles")
    public ResponseEntity<List<VehicleDTO>> getAllUniqueVehicles() {
        List<VehicleDTO> vehiclesList = vehicleService.getAllUniqueVehicles();
        return new ResponseEntity<>(vehiclesList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) throws VehicleNotFoundException {
        VehicleDTO vehicle = vehicleService.getVehicleById(id);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
        VehicleDTO vehicle = vehicleService.createVehicle(vehicleDTO);
        return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) throws VehicleNotFoundException {
        VehicleDTO updatedVehicle = vehicleService.updateVehicle(id, vehicleDTO);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) throws VehicleNotFoundException {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

}
