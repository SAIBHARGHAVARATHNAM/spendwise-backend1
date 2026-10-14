package com.expensetracker.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
@Component
public class JwtUtil {
    private final Key key; private final long expMs;
    public JwtUtil(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expMs = expMs;
    }
    public String generate(Long userId) {
        return Jwts.builder().setSubject(String.valueOf(userId)).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expMs)).signWith(key, SignatureAlgorithm.HS256).compact();
    }
    public Long parse(String token) {
        return Long.valueOf(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject());
    }
}
