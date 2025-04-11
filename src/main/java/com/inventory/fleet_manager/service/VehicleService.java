package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findById(id);
        if (existingVehicle.isPresent()) {
            vehicle.setId(id);
            return vehicleRepository.save(vehicle);
        } else {
            throw new RuntimeException("Vehicle not found");
        }
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

}
