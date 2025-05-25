package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.dto.AnalyticsResponse;
import com.inventory.fleet_manager.repository.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    public AnalyticsResponse getMonthlySales(Map<String, String> filters) {
        return analyticsRepository.fetchMonthlySales(filters);
    }

    public AnalyticsResponse getTopModelSold(Map<String, String> filters) {
        return analyticsRepository.fetchTopModelSold(filters);
    }
}