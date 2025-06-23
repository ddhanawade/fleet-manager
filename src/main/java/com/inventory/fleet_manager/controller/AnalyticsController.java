package com.inventory.fleet_manager.controller;

import com.inventory.fleet_manager.dto.AnalyticsResponse;
import com.inventory.fleet_manager.dto.MonthlySalesRequest;
import com.inventory.fleet_manager.dto.MonthlySalesResponse;
import com.inventory.fleet_manager.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @PostMapping("/monthly-sales")
    public List<MonthlySalesResponse> getMonthlySalesReport(@RequestBody MonthlySalesRequest request) {
        // Validate required fields
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required parameters");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be less than end date");
        }
        return analyticsService.getMonthlySales(request);
    }

    @GetMapping("/top-model-sold")
    public AnalyticsResponse getTopModelSold(@RequestBody MonthlySalesRequest request) {
        // Validate required fields
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required parameters");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be less than end date");
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", String.valueOf(request.getStartDate()));
        filters.put("endDate", String.valueOf(request.getEndDate()));
        filters.put("city", request.getCity());
        if (request.getMake() != null && !request.getMake().isEmpty()) {
            filters.put("make", request.getMake());
        }
        if (request.getModel() != null && !request.getModel().isEmpty()) {
            filters.put("model", request.getModel());
        }

        return analyticsService.getTopModelSold(filters);
    }
}