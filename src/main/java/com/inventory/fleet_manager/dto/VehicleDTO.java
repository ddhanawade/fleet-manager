package com.inventory.fleet_manager.dto;

import java.util.Date;

public class VehicleDTO {
    private String make;
    private String model;
    private String grade;
    private String fuelType;
    private String exteriorColor;
    private String interiorColor;
    private String location;
    private String chassisNumber;
    private String engineNumber;
    private Integer keyNumber;
    private String status;
    private Date receivedDate;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getExteriorColor() {
        return exteriorColor;
    }

    public void setExteriorColor(String exteriorColor) {
        this.exteriorColor = exteriorColor;
    }

    public String getInteriorColor() {
        return interiorColor;
    }

    public void setInteriorColor(String interiorColor) {
        this.interiorColor = interiorColor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public Integer getKeyNumber() {
        return keyNumber;
    }

    public void setKeyNumber(Integer keyNumber) {
        this.keyNumber = keyNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    @Override
    public String toString() {
        return "VehicleDTO{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", grade='" + grade + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", exteriorColor='" + exteriorColor + '\'' +
                ", interiorColor='" + interiorColor + '\'' +
                ", location='" + location + '\'' +
                ", chassisNumber='" + chassisNumber + '\'' +
                ", engineNumber='" + engineNumber + '\'' +
                ", keyNumber=" + keyNumber +
                ", status='" + status + '\'' +
                ", receivedDate=" + receivedDate +
                '}';
    }
}
