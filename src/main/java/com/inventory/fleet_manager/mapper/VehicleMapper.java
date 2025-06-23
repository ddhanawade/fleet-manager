package com.inventory.fleet_manager.mapper;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.model.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleDTO toDTO(Vehicle vehicle);

    Vehicle toEntity(VehicleDTO vehicleDTO);
}