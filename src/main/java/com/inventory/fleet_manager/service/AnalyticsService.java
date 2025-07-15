package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.dto.*;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    public List<MonthlySalesResponse> getMonthlySales(MonthlySalesRequest request) {
        // Build filters based on request parameters
        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", request.getStartDate().toString());
        filters.put("endDate", request.getEndDate().toString());
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            filters.put("city", request.getCity());
        }
        if (request.getMake() != null && !request.getMake().isEmpty()) {
            filters.put("make", request.getMake());
        }
        if (request.getModel() != null && !request.getModel().isEmpty()) {
            filters.put("model", request.getModel());
        }
        if (request.getLeadName() != null && !request.getLeadName().isEmpty()) {
            filters.put("leadName", request.getLeadName());
        }
        if (request.getSalesPersonName() != null && !request.getSalesPersonName().isEmpty()) {
            filters.put("salesPersonName", request.getSalesPersonName());
        }

        return analyticsRepository.getMonthlySalesReport(filters);
    }

    public AnalyticsResponse getTopModelSold(Map<String, String> filters) {
        return analyticsRepository.fetchTopModelSold(filters);
    }

    public List<MonthlyPurchaseResponse> getVehiclesPurchased(MonthlyPurchasedRequest request) {
        Map<String, String> filters = new HashMap<>();
        filters.put("startDate", request.getStartDate().toString());
        filters.put("endDate", request.getEndDate().toString());
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            filters.put("city", request.getCity());
        }
        if (request.getMake() != null && !request.getMake().isEmpty()) {
            filters.put("make", request.getMake());
        }
        if (request.getModel() != null && !request.getModel().isEmpty()) {
            filters.put("model", request.getModel());
        }

        return analyticsRepository.fetchVehiclesPurchased(filters);
    }
}