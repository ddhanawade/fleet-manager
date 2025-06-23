package com.inventory.fleet_manager.repository;

import com.inventory.fleet_manager.model.ModelInfo;
import com.inventory.fleet_manager.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<ModelInfo, Long> {

}
