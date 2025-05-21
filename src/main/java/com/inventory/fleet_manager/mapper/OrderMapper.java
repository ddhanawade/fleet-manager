package com.inventory.fleet_manager.mapper;

import com.inventory.fleet_manager.dto.OrderDTO;
import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.model.Order;
import com.inventory.fleet_manager.model.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toDTO(Order order);

    Order toEntity(OrderDTO orderDTO);
}