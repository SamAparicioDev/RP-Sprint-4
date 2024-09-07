package com.example.reactive_gateway;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(!exchange.getRequest().getPath().toString().startsWith("/api/auth")){
            if (token == null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    if(jwtUtil.validateToken(token)){
                        Claims claims = jwtUtil.parseClaims(token);
                        exchange.getRequest().mutate().header("username", claims.getSubject()).build();

                    }
                    else{
                        return onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
                    }

                }catch (Exception e){
                    return onError(exchange, "JWT Token validation failed", HttpStatus.UNAUTHORIZED);
                }

            }
            else{
                return onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
            }


        }


        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String e, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}
