package com.hotel.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.hotel.model.Usuario;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Responsabilidad única: Manejo de tokens JWT 
public class JwtUtil {
    private static final String SECRET = "K9H8G7F6E5D4C3B2A1Z9Y8X7W6V5U4T3S2R1";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION_TIME = 86400000; // 24 horas

    public static String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("email", usuario.getEmail());
        claims.put("rolId", usuario.getRolId());

        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        System.out.println("Fecha actual: " + now);
        System.out.println("Fecha expiración: " + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getEmailFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }
}