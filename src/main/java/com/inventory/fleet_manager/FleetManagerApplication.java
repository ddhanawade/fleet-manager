package com.inventory.fleet_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.inventory.fleet_manager", excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.inventory.fleet_manager.configuration.DatasourceProperties.WebSecurityConfig.class)
})
public class FleetManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetManagerApplication.class, args);
	}

}
