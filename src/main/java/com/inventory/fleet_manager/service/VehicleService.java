package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.mapper.VehicleMapper;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public List<VehicleDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleDTO> vehicleDTOList =  vehicles.stream().map(vehicleMapper::toDTO).collect(Collectors.toList());
        return vehicleDTOList;
    }

    public VehicleDTO getVehicleById(Long id) throws VehicleNotFoundException {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty()) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        return vehicleMapper.toDTO(vehicle.get());
    }

    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        Vehicle savedVehicle = vehicleRepository.save(vehicleMapper.toEntity(vehicleDTO));
        return vehicleMapper.toDTO(savedVehicle);
    }

    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) throws VehicleNotFoundException {
        if (vehicleDTO == null) {
            throw new IllegalArgumentException("VehicleDTO cannot be null");
        }

        // Retrieve the existing vehicle
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

        // Update only the fields provided in the DTO
        if (vehicleDTO.getMake() != null) {
            existingVehicle.setMake(vehicleDTO.getMake());
        }
        if (vehicleDTO.getModel() != null) {
            existingVehicle.setModel(vehicleDTO.getModel());
        }
        if (vehicleDTO.getGrade() != null) {
            existingVehicle.setGrade(vehicleDTO.getGrade());
        }
        if (vehicleDTO.getFuelType() != null) {
            existingVehicle.setFuelType(vehicleDTO.getFuelType());
        }
        if (vehicleDTO.getExteriorColor() != null) {
            existingVehicle.setExteriorColor(vehicleDTO.getExteriorColor());
        }
        if (vehicleDTO.getInteriorColor() != null) {
            existingVehicle.setInteriorColor(vehicleDTO.getInteriorColor());
        }
        if (vehicleDTO.getLocation() != null) {
            existingVehicle.setLocation(vehicleDTO.getLocation());
        }
        if (vehicleDTO.getStatus() != null) {
            existingVehicle.setStatus(vehicleDTO.getStatus());
        }
        if (vehicleDTO.getChassisNumber() != null) {
            existingVehicle.setChassisNumber(vehicleDTO.getChassisNumber());
        }
        if (vehicleDTO.getEngineNumber() != null) {
            existingVehicle.setEngineNumber(vehicleDTO.getEngineNumber());
        }
        if (vehicleDTO.getKeyNumber() != null) {
            existingVehicle.setKeyNumber(vehicleDTO.getKeyNumber());
        }
        if (vehicleDTO.getReceivedDate() != null) {
            existingVehicle.setReceivedDate(vehicleDTO.getReceivedDate());
        }

        // Save the updated entity
        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);

        // Map the updated entity back to DTO
        return vehicleMapper.toDTO(updatedVehicle);
    }

    public void deleteVehicle(Long id) throws VehicleNotFoundException {
        if (vehicleRepository.findById(id).isEmpty()) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public List<VehicleDTO> getAllUniqueVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream()
                .map(vehicleMapper::toDTO)
                .filter(distinctByKey(VehicleDTO::getModel)) // Unique by model
                .collect(Collectors.toList());
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}