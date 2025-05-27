package com.inventory.fleet_manager.controller;

import com.inventory.fleet_manager.dto.AnalyticsResponse;
import com.inventory.fleet_manager.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/monthly-sales")
    public AnalyticsResponse getMonthlySalesReport(
            @RequestParam String dateRange,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model) {
        // Parse dateRange into startDate and endDate
        String[] dates = dateRange.split("_to_");
        if (dates.length != 2) {
            throw new IllegalArgumentException("Invalid dateRange format");
        }
        LocalDate startDateParsed = LocalDate.parse(dates[0]);
        LocalDate endDateParsed = LocalDate.parse(dates[1]);

        if (startDateParsed.isAfter(endDateParsed)) {
            throw new IllegalArgumentException("Start date must be less than end date");
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", startDateParsed.toString());
        filters.put("endDate", endDateParsed.toString());
        filters.put("city", city);
        if (make != null && !make.isEmpty()) {
            filters.put("brandName", make);
        }
        if (model != null && !model.isEmpty()) {
            filters.put("modelName", model);
        }

        return analyticsService.getMonthlySales(filters);
    }

    @GetMapping("/top-model-sold")
    public AnalyticsResponse getTopModelSold(
            @RequestParam String dateRange,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model) {

        // Parse dateRange into startDate and endDate
        String[] dates = dateRange.split("_to_");
        if (dates.length != 2) {
            throw new IllegalArgumentException("Invalid dateRange format");
        }
        LocalDate startDate = LocalDate.parse(dates[0]);
        LocalDate endDate = LocalDate.parse(dates[1]);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be less than end date");
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", String.valueOf(startDate));
        filters.put("endDate", String.valueOf(endDate));
        filters.put("city", city);

        if (make != null && !make.isEmpty()) {
            filters.put("make", make);
        }
        if (model != null && !model.isEmpty()) {
            filters.put("model", model);
        }

        return analyticsService.getTopModelSold(filters);
    }
}