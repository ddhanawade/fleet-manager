package com.inventory.fleet_manager.dto;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CsvToApiInserter {

    private static final String API_URL = "http://localhost:8080/api/vehicles";

    public static void main(String[] args) {
        String csvFilePath = "/Users/ddhanawade/Desktop/Backend-Project/fleet-manager/src/main/resources/vehicleStock.csv"; // Path to your CSV file
        try {
            // Read CSV file and map to VehicleDTO objects
            List<VehicleDTO> vehicles = readCsvToVehicleDTO(csvFilePath);
            System.out.println("vehicleList ::::: " + vehicles);
            // Insert each vehicle into the database via the API
            RestTemplate restTemplate = new RestTemplate();
            for (VehicleDTO vehicle : vehicles) {
                ResponseEntity<VehicleDTO> response = restTemplate.postForEntity(API_URL, vehicle, VehicleDTO.class);
                System.out.println("Inserted vehicle: " + response.getBody());
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inserting vehicle: " + e.getMessage());
        }
    }

    private static List<VehicleDTO> readCsvToVehicleDTO(String csvFilePath) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.builder()
                .addColumn("invoiceDate")
                .addColumn("invoiceNumber")
                .addColumn("purchaseDealer")
                .addColumn("receivedDate")
                .addColumn("manufactureDate")
                .addColumn("model")
                .addColumn("grade")
                .addColumn("fuelType")
                .addColumn("suffix")
                .addColumn("exteriorColor")
                .addColumn("interiorColor")
                .addColumn("chassisNumber")
                .addColumn("engineNumber")
                .addColumn("keyNumber")
                .addColumn("location")
                .addColumn("tkmInvoiceValue")
                .addColumn("age")
                .addColumn("interest")
                .addColumn("status")
                .addColumn("make")
                .setUseHeader(true)
                .build();
        MappingIterator<VehicleDTO> iterator = csvMapper.readerFor(VehicleDTO.class).with(schema).readValues(new File(csvFilePath));
        return iterator.readAll();
    }
}