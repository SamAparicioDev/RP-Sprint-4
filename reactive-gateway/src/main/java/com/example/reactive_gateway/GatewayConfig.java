package com.example.reactive_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("task.controller", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://user.webflux"))
                .route("auth.controller", r -> r.path("/api/auth/**")
                        .uri("lb://user.webflux"))
                .route("task.controller", r -> r.path("/api/tasks/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://task.webflux"))
                .build();
    }
}
