package com.inventory.fleet_manager.repository;

import com.inventory.fleet_manager.model.TestDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {
}