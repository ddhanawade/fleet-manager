package com.inventory.fleet_manager.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yy");
    private static final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        try {
            return outputFormat.parse(outputFormat.format(inputFormat.parse(date)));
        } catch (ParseException e) {
            throw new IOException("Failed to parse date: " + date, e);
        }
    }
}
