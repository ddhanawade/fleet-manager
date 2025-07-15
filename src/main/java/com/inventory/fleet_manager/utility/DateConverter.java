package com.inventory.fleet_manager.utility;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateConverter {
    public static String convertInvoiceDate(String invoiceDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(invoiceDate, inputFormatter);
            return zonedDateTime.format(outputFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error parsing invoice date: " + invoiceDate, e);
        }
    }
}
