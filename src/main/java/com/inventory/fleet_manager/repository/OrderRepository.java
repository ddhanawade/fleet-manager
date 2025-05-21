package com.inventory.fleet_manager.repository;

import com.inventory.fleet_manager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select o from Order o where o.vehicleId = ?1""")
    List<Order> findByVehicleId(Long vehicleId);
}
