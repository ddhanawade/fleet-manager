package com.inventory.fleet_manager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inventory.fleet_manager.enums.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String customerName;
    private String phoneNumber;
    private String leadName;
    private  String salesPersonName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date orderDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date deliveryDate;
    private String financerName;
    private String financeType;
    private String remarks;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long vehicleId;
    @Enumerated(EnumType.STRING)
    private orderStatus orderStatus;
    @Enumerated(EnumType.STRING)
    private DmsStatus dmsStatus;
    private String dealAmount;
    @Enumerated(EnumType.STRING)
    private status vehicleStatus;

}
