package com.inventory.fleet_manager.dto;

import java.time.LocalDate;

public class MonthlySalesResponse {
    private String model;
    private LocalDate orderDate;
    private String location;
    private double invoiceValue;

    // Constructor
    public MonthlySalesResponse(String model, LocalDate orderDate, String location, double invoiceValue) {
        this.model = model;
        this.orderDate = orderDate;
        this.location = location;
        this.invoiceValue = invoiceValue;
    }

    // Getters and Setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getInvoiceValue() {
        return invoiceValue;
    }

    public void setInvoiceValue(double invoiceValue) {
        this.invoiceValue = invoiceValue;
    }
}