package com.inventory.fleet_manager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {
    @Bean(name = "jwtUtilConfiguration")
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}