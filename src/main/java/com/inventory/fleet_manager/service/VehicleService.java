package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
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

    public Vehicle getVehicleById(Long id) throws VehicleNotFoundException {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty()) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        return vehicle.get();
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) throws VehicleNotFoundException {
        Vehicle existingVehicle = getVehicleById(id);
        existingVehicle.setMake(vehicle.getMake());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setGrade(vehicle.getGrade());
        existingVehicle.setFuelType(vehicle.getFuelType());
        existingVehicle.setExteriorColor(vehicle.getExteriorColor());
        existingVehicle.setInteriorColor(vehicle.getInteriorColor());
        existingVehicle.setChassisNumber(vehicle.getChassisNumber());
        existingVehicle.setEngineNumber(vehicle.getEngineNumber());
        existingVehicle.setKeyNumber(vehicle.getKeyNumber());
        existingVehicle.setLocation(vehicle.getLocation());
        existingVehicle.setStatus(vehicle.getStatus());
        existingVehicle.setReceivedDate(vehicle.getReceivedDate());
        return vehicleRepository.save(existingVehicle);
    }

    public void deleteVehicle(Long id) throws VehicleNotFoundException {
        if (vehicleRepository.findById(id).isEmpty()) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}