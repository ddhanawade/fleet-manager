package com.inventory.fleet_manager.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private List<String> allowedOrigins;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        private final CorsProperties corsProperties;

        public WebConfig(CorsProperties corsProperties) {
            this.corsProperties = corsProperties;
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            System.out.println("Web config corsProperties.getAllowedOrigins() = " + corsProperties.getAllowedOrigins());
            registry.addMapping("/**")
                    .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0])) // Convert List to String array
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedOrigins("http://localhost:4200")
                    .allowedHeaders("*")
                    .allowCredentials(true);
        }
    }
}