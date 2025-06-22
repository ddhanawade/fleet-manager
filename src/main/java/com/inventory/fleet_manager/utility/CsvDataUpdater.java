package com.inventory.fleet_manager.utility;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CsvDataUpdater {

    private static final String CSV_FILE_PATH = "/Users/prai4/Desktop/Backend 2/fleet-manager/src/main/resources/vehicleStock.csv";

    public static void main(String[] args) {
        CsvDataUpdater updater = new CsvDataUpdater();
        updater.updateDatabaseFromCsv();
    }

    public void updateDatabaseFromCsv() {
        try (CSVReader csvReader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            String[] nextLine;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

            // Skip the header row
            csvReader.readNext();

            while ((nextLine = csvReader.readNext()) != null) {
                VehicleDTO vehicleDTO = new VehicleDTO();
                try {
                    String rawDate = nextLine[0].trim(); // Assuming invoiceDate is in the first column
                    Date invoiceDate = dateFormat.parse(rawDate);
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