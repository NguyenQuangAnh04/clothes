package com.example.clothes.component;

import com.example.clothes.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(singingKey(secret), SignatureAlgorithm.HS256)
                .compact();
        return token;
    }
    private Key singingKey(String secret) {
        byte[] bytes = java.util.Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public Long extractUserId(String token){
        return extractClaims(token, claims -> Long.parseLong(claims.get("userId").toString()));
    }
    private Date isExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public boolean validate(String token, UserDetails userDetails) {
        String username = userDetails.getUsername();
        return (username.equals(extractUsername(token)) && !isExpiration(token).before(new Date()));
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(singingKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
