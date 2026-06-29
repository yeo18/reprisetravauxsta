package com.example.demo.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Générer un JWT contenant l'email de l'utilisateur
     */
    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 86400000)
                )
                .signWith(key)
                .compact();
    }

    /**
     * Extraire l'email contenu dans le token
     */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Vérifier si le token est valide
     */
    public boolean isTokenValid(String token) {

        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lire les informations du token
     */
    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}