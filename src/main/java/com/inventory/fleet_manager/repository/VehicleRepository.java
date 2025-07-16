package com.inventory.fleet_manager.repository;

import com.inventory.fleet_manager.dto.ModelInfoDTO;
import com.inventory.fleet_manager.dto.VehicleOrderResponse;
import com.inventory.fleet_manager.model.Vehicle;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    @Query("SELECT new com.inventory.fleet_manager.dto.VehicleOrderResponse(" +
            "v.id, v.make, v.model, v.grade, v.fuelType, v.exteriorColor, v.interiorColor, " +
            "v.chassisNumber, v.engineNumber, v.keyNumber, v.location, v.vehicleStatus, " +
            "v.receivedDate, v.invoiceDate, v.invoiceNumber, v.purchaseDealer, v.manufactureDate, " +
            "v.suffix, v.invoiceValue, v.age, v.interest, " +
            "o.orderId, o.customerName, o.phoneNumber, o.leadName, o.salesPersonName, " +
            "o.orderDate, o.deliveryDate, o.financerName, o.financeType, o.remarks, " +
            "o.createdAt, o.updatedAt, o.createdBy, o.updatedBy, o.orderStatus, " +
            "o.dealAmount, o.dmsStatus) " +
            "FROM Vehicle v LEFT JOIN Order o ON v.id = o.vehicleId WHERE v.model = :model")
    List<VehicleOrderResponse> findVehicleAndOrderDetailsByModel(@Param("model") String model);
}