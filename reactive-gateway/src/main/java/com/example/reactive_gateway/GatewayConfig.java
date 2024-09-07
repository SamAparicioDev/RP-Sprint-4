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
                .route("user.server", route ->
                        route.path("/api/v2/user/**").filters(f -> f.filter(jwtAuthenticationFilter))
                                .uri("lb://user.webflux"))
                .route("user.server.register", route->
                        route.path("/api/v2/user/save")
                                .uri("lb://user.webflux"))
                .route("auth.serve", route ->
                        route.path("/api/v2/auth/**")
                                .uri("lb://user.webflux"))
                .route("task.server", route ->
                route.path("/api/v2/task/**").filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://task.webflux"))
                .build();
    }
}
