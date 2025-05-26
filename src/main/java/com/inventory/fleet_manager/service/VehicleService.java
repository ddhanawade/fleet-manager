package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.mapper.VehicleMapper;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.VehicleRepository;
import com.inventory.fleet_manager.utility.VehicleUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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


    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper, VehicleUtils vehicleUtils) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public List<VehicleDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream()
                .map(vehicle -> {
                    VehicleDTO dto = vehicleMapper.toDTO(vehicle);

                    // Calculate and set age
                    Integer ageString = (VehicleUtils.calculateVehicleAge(String.valueOf(vehicle.getReceivedDate())));
                    dto.setAge(ageString);

                    // Calculate and set interest
//                    String interest = VehicleUtils.calculateInterest(vehicle.getInvoiceValue(), dto.getAge());
//                    dto.setInterest(interest.equals("Invalid input") ? null : interest);

                    return dto;
                })
                .collect(Collectors.toList());
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

    public Map<String, Map<String, Long>> getAgeCountByModel() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        return vehicles.stream()
                .collect(Collectors.groupingBy(
                        Vehicle::getModel, // Group by model
                        Collectors.groupingBy(vehicle -> {
                            Integer age = VehicleUtils.calculateVehicleAge(String.valueOf(vehicle.getReceivedDate()));
                            if (age == null) {
                                return "Unknown";
                            } else if (age < 30) {
                                return "Less than 30 days";
                            } else if (age <= 60) {
                                return "30 to 60 days";
                            } else {
                                return "Greater than 60 days";
                            }
                        }, Collectors.counting()) // Count vehicles in each age range
                ));
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    public List<VehicleDTO> getAllUniqueVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Group vehicles by model and calculate age counts
        Map<String, Map<String, Long>> ageCountsByModel = vehicles.stream()
                .collect(Collectors.groupingBy(
                        Vehicle::getModel,
                        Collectors.groupingBy(vehicle -> {
                            Integer age = VehicleUtils.calculateVehicleAge(String.valueOf(vehicle.getReceivedDate()));
                            if (age == null) {
                                return "Unknown";
                            } else if (age < 30) {
                                return "Less than 30 days";
                            } else if (age <= 60) {
                                return "30 to 60 days";
                            } else {
                                return "Greater than 60 days";
                            }
                        }, Collectors.counting())
                ));

        // Map unique vehicles to DTOs and set age counts
        return vehicles.stream()
                .filter(distinctByKey(Vehicle::getModel)) // Unique by model
                .map(vehicle -> {
                    VehicleDTO dto = vehicleMapper.toDTO(vehicle);

                    Map<String, Long> ageCounts = ageCountsByModel.getOrDefault(vehicle.getModel(), Map.of());
                    dto.setLessThan30DaysCount(ageCounts.getOrDefault("Less than 30 days", 0L));
                    dto.setBetween30And60DaysCount(ageCounts.getOrDefault("30 to 60 days", 0L));
                    dto.setGreaterThan60DaysCount(ageCounts.getOrDefault("Greater than 60 days", 0L));

                    return dto;
                })
                .collect(Collectors.toList());
    }
}