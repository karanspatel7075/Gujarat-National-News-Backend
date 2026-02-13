package com.gnn.newsnetwork.GnnNewsNetworkApplication.auth;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthTokenGen {

    @Value("${secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Users users) {
        return Jwts.builder()
                .setSubject(users.getUsername())
                .claim("userId", users.getId())
                .claim("roles", users.getRole())
//                .claim("roles", "ROLE_" + users.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (jwtExpiration)))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256) // new Signature Algorithm
                .compact();
    }

    public String getUserFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
