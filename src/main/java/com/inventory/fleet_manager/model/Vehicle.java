package com.inventory.fleet_manager.model;

import com.inventory.fleet_manager.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    @Column(unique = true)
    private String chassisNumber;
    private String engineNumber;
    private String keyNumber;
    private String location;
    @Enumerated(EnumType.STRING)
    private status vehicleStatus;
    private Date receivedDate;
    private Date invoiceDate;
    private String invoiceNumber;
    private String purchaseDealer;
    private String manufactureDate;
    private String suffix;
    private String invoiceValue;
    private  Integer age;
    private String interest;
    private String remarks;

    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;

    private String createdBy;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    private String updatedBy;

    @PrePersist
    public void onPrePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
}