package com.inventory.fleet_manager.controller;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.dto.VehicleOrderResponse;
import com.inventory.fleet_manager.enums.status;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.mapper.VehicleMapper;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
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

    @GetMapping("/ageCountByModel")
    public ResponseEntity<Map<String, Map<String, Long>>> getAgeCountByModel() {
        Map<String, Map<String, Long>> ageCountByModel = vehicleService.getAgeCountByModel();
        return new ResponseEntity<>(ageCountByModel, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            vehicleService.saveVehiclesFromFile(file);
            Map<String, String> response = Map.of("message", "File uploaded and data saved successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Error processing file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @GetMapping("/vehiclesAndOrderDetailsByModel")
    public List<VehicleOrderResponse> getVehicleAndOrderDetailsByModel(@RequestParam String model) {
        return vehicleService.getVehicleAndOrderDetailsByModel(model);
    }
}
