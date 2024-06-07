package com.example.shopping_mall_web.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private SecretKey secretKey;
    private SecretKey refreshSecretKey;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        if (decodedKey.length < 32) { // 256 bits / 8 bits per byte = 32 bytes
            throw new IllegalArgumentException("The secret key must be at least 256 bits (32 bytes)");
        }
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    @Value("${jwt.refresh-secret}")
    public void setRefreshSecret(String refreshSecret) {
        byte[] decodedKey = Base64.getDecoder().decode(refreshSecret);
        if (decodedKey.length < 32) { // 256 bits / 8 bits per byte = 32 bytes
            throw new IllegalArgumentException("The refresh secret key must be at least 256 bits (32 bytes)");
        }
        this.refreshSecretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username, String name, String address) {
        Map<String, Object> claims = new HashMap<>();
        if (name != null) {
            claims.put("name", name);
        }
        if (address != null) {
            claims.put("address", address);
        }
        return createToken(claims, username, secretKey, 1000 * 60 * 60 * 10); // 10시간 만료
    }

    public String generateRefreshToken(String username) {
        return createToken(new HashMap<>(), username, refreshSecretKey, 1000 * 60 * 60 * 24 * 7); // 1주일 만료
    }

    private String createToken(Map<String, Object> claims, String subject, Key signingKey, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshSecretKey).build().parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    public Map<String, Object> decodeToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return new HashMap<>(claims);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode token", e);
        }
    }
}
