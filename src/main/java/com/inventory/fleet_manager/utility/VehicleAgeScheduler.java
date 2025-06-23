package com.inventory.fleet_manager.utility;

import com.inventory.fleet_manager.dto.VehicleDTO;

import com.inventory.fleet_manager.enums.status;
import com.inventory.fleet_manager.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
//    @Scheduled(cron = "0 */1 * * * ?") // Runs every 10 minutes// Runs at midnight every day
    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    public void updateVehicleAgesAndInterests() {
        log.info("Starting vehicle age update process on: {}", java.time.LocalDate.now());
        boolean ageUpdateSuccessful = true;

        try {
            List<VehicleDTO> vehicles = vehicleService.getAllVehiclesWithoutThreading();
            log.info("Retrieved {} vehicles for age update.", vehicles.size());

            // Update the age of each vehicle
            for (VehicleDTO vehicle : vehicles) {
                if (vehicle == null) {
                    log.warn("Skipping null vehicle in the list.");
                    continue;
                }
                try {
                    if (vehicle.getVehicleStatus() != null && vehicle.getVehicleStatus().equals(status.SOLD.name())) {
                        log.info("Skipping age update for vehicle with ID: {} as it is SOLD.", vehicle.getId());
                        continue;
                    }
                    if (vehicle.getInvoiceDate() != null) {
                        String invoiceDateString = VehicleUtils.extractDate(String.valueOf(vehicle.getInvoiceDate()));
                        Integer age = VehicleUtils.calculateVehicleAge(invoiceDateString);
                        if (age != null) {
                            vehicle.setAge(age);
                            vehicleService.updateVehicle(vehicle.getId(), vehicle);
                        }
                    } else {
                        log.warn("Skipping vehicle with ID: {} as receivedDate is null.", vehicle.getId());
                    }
                } catch (Exception e) {
                    ageUpdateSuccessful = false;
                    log.error("Error updating age for vehicle with ID: {}", vehicle.getId(), e);
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
                List<VehicleDTO> vehicles = vehicleService.getAllVehiclesWithoutThreading();

                for (VehicleDTO vehicle : vehicles) {
                    if (vehicle == null) {
                        log.warn("Skipping null vehicle in the list.");
                        continue;
                    }
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
        log.info("Vehicle age and interest update process completed on: {}", java.time.LocalDate.now());
    }
}