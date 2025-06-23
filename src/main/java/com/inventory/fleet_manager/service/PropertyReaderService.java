package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.configuration.DatasourceProperties;
import org.springframework.stereotype.Service;

@Service
public class PropertyReaderService {

    private final DatasourceProperties datasourceProperties;

    public PropertyReaderService(DatasourceProperties datasourceProperties) {
        this.datasourceProperties = datasourceProperties;
    }

    public void printProperties() {
        System.out.println("Datasource URL: " + datasourceProperties.getUrl());
        System.out.println("Datasource Username: " + datasourceProperties.getUsername());
        System.out.println("Datasource Password: " + datasourceProperties.getPassword());
    }
}