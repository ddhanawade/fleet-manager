package com.inventory.fleet_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnalyticsResponse {
    private List<Object[]> data;
}