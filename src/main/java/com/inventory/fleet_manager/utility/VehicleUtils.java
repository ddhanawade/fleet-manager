package com.inventory.fleet_manager.utility;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
    public static Integer calculateVehicleAge(String receivedDate) {
        if (receivedDate == null || receivedDate.isEmpty()) {
            return null; // Return null if receivedDate is null or empty
        }

        try {
            // Extract only the date part
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDate receivedLocalDate = LocalDate.parse(receivedDate, formatter);
            LocalDate currentDate = LocalDate.now();

            // Calculate the number of days between the received date and the current date
            return (int) ChronoUnit.DAYS.between(receivedLocalDate, currentDate);
        } catch (Exception e) {
            return null; // Return null if parsing fails
        }
    }

    private static String extractDate(String receivedDate) {
        // Parse the timestamp into LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime dateTime = LocalDateTime.parse(receivedDate, formatter);

        // Extract only the date
        String extractedDate = dateTime.toLocalDate().toString();

        System.out.println("Date: " + extractedDate); // Output: Date: 2024-09-03
        return extractedDate; // Return as is if no 'T' present
    }
}
