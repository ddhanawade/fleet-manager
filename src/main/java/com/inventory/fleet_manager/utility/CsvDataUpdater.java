package com.inventory.fleet_manager.utility;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.service.VehicleService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class CsvDataUpdater {

    @Autowired
    private VehicleService vehicleService;

    private static final String CSV_FILE_PATH = "src/main/resources/vehicle_sql_data.csv";

    @Transactional
    public void updateDatabaseFromCsv() {
        try (CSVReader csvReader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            String[] line;
            csvReader.readNext(); // Skip the header row

            while ((line = csvReader.readNext()) != null) {
                Long id = Long.parseLong(line[0]);
                String invoiceDateStr = line[1];

                // Convert the Date object to a String in the required format
                String formattedInvoiceDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yy").parse(invoiceDateStr));

                // Create a VehicleDTO object and set the invoiceDate
                VehicleDTO vehicleDTO = new VehicleDTO();
                vehicleDTO.setManufactureDate(formattedInvoiceDate);

                // Call the updateVehicle method
                vehicleService.updateVehicle(id, vehicleDTO);
            }

            System.out.println("Database updated successfully from CSV.");
        } catch (IOException | CsvValidationException | ParseException e) {
            System.err.println("Error while updating database from CSV: " + e.getMessage());
        }
    }
}