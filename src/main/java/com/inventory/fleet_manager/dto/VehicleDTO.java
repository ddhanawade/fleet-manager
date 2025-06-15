package com.inventory.fleet_manager.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.inventory.fleet_manager.enums.status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date invoiceDate;
    private String invoiceNumber;
    private String purchaseDealer;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date receivedDate;
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
    private String invoiceValue;
    private  Integer age;
    private String interest;
    @Enumerated(EnumType.STRING)
    @JsonAlias("status")
    private status vehicleStatus;
    private String make;
    private Long lessThan30DaysCount;
    private Long between30And60DaysCount;
    private Long greaterThan60DaysCount;
}
