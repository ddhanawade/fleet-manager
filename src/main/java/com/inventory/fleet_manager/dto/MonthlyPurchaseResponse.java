package com.inventory.fleet_manager.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class MonthlyPurchaseResponse {
    private String model;
    private String location;
    private double invoiceValue;
    private String make;
    private String chassisNumber;
    private String engineNumber;
    private String keyNumber;
    private String purchaseDealer;
    private Date invoiceDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}