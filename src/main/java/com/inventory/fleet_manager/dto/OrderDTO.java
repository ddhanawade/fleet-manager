package com.inventory.fleet_manager.dto;

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
    private Integer phoneNumber;
    private String leadName;
    private  String salesPersonName;
    private Date orderDate;
    private Date deliveryDate;
    private String financerName;
    private String financeType;
    private String remarks;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long vehicleId;
    private String status;
}
