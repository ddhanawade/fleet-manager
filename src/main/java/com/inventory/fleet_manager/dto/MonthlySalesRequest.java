package com.inventory.fleet_manager.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MonthlySalesRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String city;
    private String make;
    private String model;
    private String leadName;
    private String salesPersonName;
}