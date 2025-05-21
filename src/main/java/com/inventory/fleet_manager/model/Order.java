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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
