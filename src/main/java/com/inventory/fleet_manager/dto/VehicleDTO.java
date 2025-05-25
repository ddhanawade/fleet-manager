package com.inventory.fleet_manager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {
    private Long id;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yy")
    private String invoiceDate;
    private String invoiceNumber;
    private String purchaseDealer;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yy")
    private Date receivedDate;
   // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yy")
    private String manufactureDate;
    private String model;
    private String grade;
    private String fuelType;
    private String suffix;
    private String exteriorColor;
    private String interiorColor;
    private String chassisNumber;
    private String engineNumber;
    private String keyNumber;
    private String location;
    private String tkmInvoiceValue;
    private  Integer age;
    private String interest;
    private String status;
    private String make;
}
