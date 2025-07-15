package com.inventory.fleet_manager.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class MonthlySalesResponse {
    private String model;
    //private LocalDate orderDate;
    private String location;
    private double invoiceValue;
    private String make;
    private String purchaseDealer;
    private String chassisNumber;
    private String engineNumber;
    private String keyNumber;
    private String interest;
    private String status;

    private Long orderId;
    private String customerName;
    private String phoneNumber;
    private String leadName;
    private String salesPersonName;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String financerName;
    private String financeType;
    private String remarks;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String createdBy;
    private String updatedBy;
    private String orderStatus;
}