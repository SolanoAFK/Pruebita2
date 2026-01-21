package com.civilportal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret:civil-portal-secret-key-for-jwt-token-generation-minimum-256-bits}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private Long expiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generarToken(Integer usuarioId, String username, Integer empresaId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuarioId);
        claims.put("empresaId", empresaId);
        claims.put("username", username);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }
    
    public Integer extraerUsuarioId(String token) {
        Claims claims = extraerTodosLosClaims(token);
        return claims.get("usuarioId", Integer.class);
    }
    
    public Integer extraerEmpresaId(String token) {
        Claims claims = extraerTodosLosClaims(token);
        return claims.get("empresaId", Integer.class);
    }
    
    public Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }
    
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
