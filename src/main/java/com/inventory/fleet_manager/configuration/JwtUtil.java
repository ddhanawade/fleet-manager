package com.inventory.fleet_manager.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "aP9!x@3#L$z%7^&*kQwE8rT6uYhJ"; // Replace with a strong secret key
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour in milliseconds

    // Generate a JWT token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Validate a JWT token and return claims
    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username (subject) from the token
    public static String extractUsername(String token) {
        return validateToken(token).getSubject();
    }
}