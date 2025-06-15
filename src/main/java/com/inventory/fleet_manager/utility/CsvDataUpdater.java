package com.inventory.fleet_manager.utility;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CsvDataUpdater {

    private static final String CSV_FILE_PATH = "/Users/prai4/Desktop/Backend 2/fleet-manager/src/main/resources/vehicleStock.csv";

    public void updateDatabaseFromCsv() {
        try (CSVReader csvReader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            String[] nextLine;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

            while ((nextLine = csvReader.readNext()) != null) {
                VehicleDTO vehicleDTO = new VehicleDTO();
                try {
                    Date invoiceDate = dateFormat.parse(nextLine[0]); // Assuming invoiceDate is in the first column
                    vehicleDTO.setInvoiceDate(invoiceDate);
                } catch (ParseException e) {
                    System.err.println("Error parsing date: " + nextLine[0]);
                }
                // Process other fields and save vehicleDTO to the database
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}