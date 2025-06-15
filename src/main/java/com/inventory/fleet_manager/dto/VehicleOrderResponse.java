package com.inventory.fleet_manager.dto;

import com.inventory.fleet_manager.enums.orderStatus;
import com.inventory.fleet_manager.enums.status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleOrderResponse {
    private Long vehicleId;
    private String make;
    private String model;
    private String grade;
    private String fuelType;
    private String exteriorColor;
    private String interiorColor;
    private String chassisNumber;
    private String engineNumber;
    private String keyNumber;
    private String location;
    private status vehicleStatus;
    private Date receivedDate;
    private Date invoiceDate;
    private String invoiceNumber;
    private String purchaseDealer;
    private String manufactureDate;
    private String suffix;
    private String invoiceValue;
    private Integer age;
    private String interest;

    // Fields from Order table
    private Long orderId;
    private String customerName;
    private String phoneNumber;
    private String leadName;
    private String salesPersonName;
    private Date orderDate;
    private Date deliveryDate;
    private String financerName;
    private String financeType;
    private String remarks;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
    private orderStatus orderStatus;
}