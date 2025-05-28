package com.inventory.fleet_manager.utility;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.enums.status;
import com.inventory.fleet_manager.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@Slf4j
public class VehicleAgeScheduler {

    @Autowired
    private final VehicleService vehicleService;

    public VehicleAgeScheduler(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
    // Schedule the task to run every 24 hours
    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    public void updateVehicleAgesAndInterests() {
        log.info("Starting vehicle age update process on: {}", java.time.LocalDate.now());
        boolean ageUpdateSuccessful = true;

        try {
            List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Update the age of each vehicle
            for (VehicleDTO vehicle : vehicles) {
                try {
                    if (vehicle.getStatus() != null && vehicle.getStatus().equals(status.SOLD.name())) {
                        log.info("Skipping age update for vehicle with ID: " + vehicle.getId() + " as it is SOLD.");
                        continue;
                    }
                    if (vehicle.getReceivedDate() != null) {
                        String receivedDateString = dateFormat.format(vehicle.getReceivedDate());
                        Integer age = VehicleUtils.calculateVehicleAge(receivedDateString);
                        if (age != null) {
                            vehicle.setAge(age);
                            vehicleService.updateVehicle(vehicle.getId(), vehicle);
                        }
                    }
                } catch (Exception e) {
                    ageUpdateSuccessful = false;
                    log.error("Error updating age for vehicle with ID: " + vehicle.getId(), e);
                }
            }
        } catch (Exception e) {
            ageUpdateSuccessful = false;
            log.error("Error occurred while updating vehicle ages", e);
        }

        // Only update interests if age update was successful
        if (ageUpdateSuccessful) {
            log.info("Starting vehicle interest update process on: {}", java.time.LocalDate.now());
            try {
                List<VehicleDTO> vehicles = vehicleService.getAllVehicles();

                for (VehicleDTO vehicle : vehicles) {
                    try {
                        if (vehicle.getAge() != null && vehicle.getInvoiceValue() != null) {
                            String interest = VehicleUtils.calculateInterest(vehicle.getInvoiceValue(), vehicle.getAge());
                            vehicle.setInterest(interest);
                            vehicleService.updateVehicle(vehicle.getId(), vehicle);
                        }
                    } catch (Exception e) {
                        log.error("Error updating interest for vehicle with ID: " + vehicle.getId(), e);
                    }
                }
            } catch (Exception e) {
                log.error("Error occurred while updating vehicle interests", e);
            }
        } else {
            log.warn("Skipping interest update process as age update failed.");
        }
    }
}