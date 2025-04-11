package com.inventory.fleet_manager.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String make;
    private String model;
    private Date vehicleYear;

    // Many-to-one relationship with City
//    @ManyToOne
//    @JoinColumn(name = "city_id")
//    private City city;

    private  String city;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(Date vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", vehicleYear=" + vehicleYear +
                ", city='" + city + '\'' +
                '}';
    }
}