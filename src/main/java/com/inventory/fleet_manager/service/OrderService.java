package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.dto.OrderDTO;
import com.inventory.fleet_manager.mapper.OrderMapper;
import com.inventory.fleet_manager.mapper.VehicleMapper;
import com.inventory.fleet_manager.model.Order;
import com.inventory.fleet_manager.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    // Add methods to handle business logic related to orders

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new IllegalArgumentException("VehicleDTO cannot be null");
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        if (orderDTO.getLeadName() != null) {
            order.setLeadName(orderDTO.getLeadName());
        }
        if (orderDTO.getCustomerName() != null) {
            order.setCustomerName(orderDTO.getCustomerName());
        }
        if (orderDTO.getFinancerName() != null) {
            order.setFinancerName(orderDTO.getFinancerName());
        }
        if (orderDTO.getFinanceType() != null) {
            order.setFinanceType(orderDTO.getFinanceType());
        }
        if (orderDTO.getPhoneNumber() != null) {
            order.setPhoneNumber(orderDTO.getPhoneNumber());
        }
        if (orderDTO.getSalesPersonName() != null) {
            order.setSalesPersonName(orderDTO.getSalesPersonName());
        }
        if (orderDTO.getRemarks() != null) {
            order.setRemarks(orderDTO.getRemarks());
        }
        if (orderDTO.getStatus() != null) {
            order.setStatus(orderDTO.getStatus());
        }
        if (orderDTO.getDeliveryDate() != null) {
            order.setDeliveryDate(orderDTO.getDeliveryDate());
        }
        order.setOrderId(id);
        order.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDTO)
                .orElse(null);
    }

    public List<OrderDTO> getOrdersByVehicleId(Long vehicleId) {
        List<Order> orders = orderRepository.findByVehicleId(vehicleId);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

}
