package com.inventory.fleet_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private String status;
    private Date receivedDate;
    private String invoiceDate;
    private String invoiceNumber;
    private String purchaseDealer;
    private String manufactureDate;
    private String suffix;
    private String tkmInvoiceValue;
    private  Integer age;
    private String interest;
}