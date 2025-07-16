package com.inventory.fleet_manager.model;

import com.inventory.fleet_manager.enums.DmsStatus;
import com.inventory.fleet_manager.enums.orderStatus;
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
    private String phoneNumber;
    private String leadName;
    private String salesPersonName;
    private Date orderDate;
    private String dealAmount;
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
}
