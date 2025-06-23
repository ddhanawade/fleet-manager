package com.inventory.fleet_manager.configuration;

import com.inventory.fleet_manager.authService.JwtRequestFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceProperties {
    private String url;
    private String username;
    private String password;

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Configuration
    public static class WebSecurityConfig implements WebMvcConfigurer {

        private final JwtRequestFilter jwtRequestFilter;
        private final CorsProperties corsProperties;

        public WebSecurityConfig(JwtRequestFilter jwtRequestFilter, CorsProperties corsProperties) {
            this.jwtRequestFilter = jwtRequestFilter;
            this.corsProperties = corsProperties;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
            return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            System.out.println("corsProperties.getAllowedOrigins() = " + corsProperties.getAllowedOrigins());
            http.csrf(csrf -> csrf.disable())
                    .cors(cors -> cors.configurationSource(request -> {
                        var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                        corsConfig.setAllowedOrigins(corsProperties.getAllowedOrigins());
                        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                        corsConfig.setAllowCredentials(true);
                        return corsConfig;
                    }))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/login", "/auth/register", "/auth/*","/start-async-task").permitAll() // Allow /auth/{username}
                            .requestMatchers("/auth/**").authenticated()
                            .anyRequest().authenticated()
                    ).logout(logout -> logout
                            .logoutUrl("/auth/logout") // Define logout URL
                            .logoutSuccessHandler((request, response, authentication) -> {
                                response.setStatus(200);
                                response.getWriter().write("Logout successful");
                            })
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

    }
}