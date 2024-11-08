package com.bookstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for a REST API
                .cors(AbstractHttpConfigurer::disable) // Enable if accessing from different origin; configure as needed
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/", "/auth/register", "/auth/login", "/**").permitAll(); // Allow unauthenticated access to these pages
                    registry.anyRequest().authenticated(); // Require authentication for all other pages
                })
                .build();
    }
}
