package com.inventory.fleet_manager.configuration;

import com.inventory.fleet_manager.service.UserDetailsServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class LoggedInUserHolder {

    private static String loggedInUsername;

    private final UserDetailsServiceImpl userDetailsService;

    public LoggedInUserHolder(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void initializeLoggedInUsername() {
        loggedInUsername = userDetailsService.getLoggedInUsername();
    }

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }
}
