package com.gm.hrms.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    // ================= SIGN KEY =================

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ================= ACCESS TOKEN =================

    public String generateToken(String username) {
        return buildToken(username, accessExpiration);
    }

    // ================= REFRESH TOKEN =================

    public String generateRefreshToken(String username) {
        return buildToken(username, refreshExpiration);
    }

    // ================= TOKEN BUILDER =================

    private String buildToken(String username, long expiration) {

        Date now = new Date();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ================= EXTRACT USERNAME =================

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ================= VALIDATE TOKEN =================

    public boolean isTokenValid(String token, String username) {
        try {
            return username.equals(extractUsername(token))
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ================= CLAIMS =================

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }
}
