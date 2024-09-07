package com.example.reactive_gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private String secret = System.getenv("JWT_SECRET");

    public JwtUtil() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
    }
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.secretKey)
                .compact();
    }
    public boolean validateToken(String token) {
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(this.secret);
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
