package com.lgcns.aidd.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails userDetails) {
       return buildToken(userDetails, accessTokenExpirationMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshTokenExpirationMs);
    }

    private String buildToken(UserDetails userDetails, long expirationMillis) {
       CustomUserDetails customUser = (CustomUserDetails) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", customUser.getId());
        claims.put("employeeId", customUser.getId());
        claims.put("role", customUser.getAuthorities().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse("USER"));
         return Jwts.builder()
                .subject(customUser.getUsername())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSignKey())
                .compact();
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token);
    }

    public String extractUsername(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try{
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getPayload().getExpiration();
        return expiration.before(new Date());
    }

    public Integer extractAccountId(String token) {
        Object raw = parseToken(token).getPayload().get("accountId");
        return raw != null ? Integer.parseInt(raw.toString()) : null;
    }

    public Integer extractEmployeeId(String token) {
        Object raw = parseToken(token).getPayload().get("employeeId");
        return raw != null ? Integer.parseInt(raw.toString()) : null;
    }

    public String extractRole(String token) {
        Object raw = parseToken(token).getPayload().get("role");
        return raw != null ? raw.toString() : null;
    }
}
