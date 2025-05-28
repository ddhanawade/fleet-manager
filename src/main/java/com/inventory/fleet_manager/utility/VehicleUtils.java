package com.inventory.fleet_manager.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class VehicleUtils {
    public static String calculateInterest(String invoiceValue, Integer age) {
        if (invoiceValue == null || age == null || invoiceValue.isEmpty() || age < 0) {
            return "Invalid input";
        }

        try {
            double value = Double.parseDouble(invoiceValue);
            double interestRate = 0.05; // Example interest rate of 5%
            double interest = value * interestRate * age;
            return String.format("%.2f", interest);
        } catch (NumberFormatException e) {
            return "Invalid invoice value";
        }
    }


    public static Integer calculateVehicleAge(String receivedDateString) {
        try {
            // Update the formatter to handle `.000000` microseconds
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            LocalDateTime receivedDate = LocalDateTime.parse(receivedDateString, formatter);

            // Calculate the age in Days
            return (int) ChronoUnit.DAYS.between(receivedDate.toLocalDate(), LocalDate.now());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error parsing received date: " + receivedDateString, e);
        }
    }

    private static String extractDate(String receivedDate) {
        log.info("Received date: " + receivedDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime dateTime = LocalDateTime.parse(receivedDate, formatter);

        // Extract only the date
        String extractedDate = dateTime.toLocalDate().toString();

        log.info("Date: " + extractedDate); // Output: Date: 2024-09-03
        return extractedDate; // Return as is if no 'T' present
    }
}
