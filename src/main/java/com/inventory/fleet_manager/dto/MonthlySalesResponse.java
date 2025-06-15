package com.inventory.fleet_manager.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class MonthlySalesResponse {
    private String model;
    private LocalDate orderDate;
    private String location;
    private double invoiceValue;
    private String make;
    private String purchaseDealer;
    private String chassisNumber;
    private String engineNumber;
    private String keyNumber;
    private String interest;
    private String status;
}