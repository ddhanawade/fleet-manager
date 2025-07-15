package com.inventory.fleet_manager.utility;
import java.io.FileWriter;
import java.io.IOException;

public class CSVFileCreator {
    public static void main(String[] args) {
        String[] headers = {
                "id", "make", "model", "grade", "fuelType", "exteriorColor", "interiorColor",
                "chassisNumber", "engineNumber", "keyNumber", "location", "vehicleStatus",
                "receivedDate", "purchaseDealer", "manufactureDate", "remarks"
        };

        String fileName = "testdrive_data.csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            // Write headers
            writer.append(String.join(",", headers));
            writer.append("\n");

            // Optionally, add sample data rows here if needed
            // writer.append("1,Toyota,Corolla,Base,Petrol,Red,Black,CH12345,EN12345,KN12345,Delhi,AVAILABLE,2023-10-01,Dealer1,2023-01-01,No remarks\n");

            System.out.println("CSV file created successfully: " + fileName);
        } catch (IOException e) {
            System.err.println("Error while creating CSV file: " + e.getMessage());
        }
    }
}