package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.model.City;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.CityRepository;
import com.inventory.fleet_manager.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
}
