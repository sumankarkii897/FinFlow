package com.finflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String JWT_SECRET_KEY;

    @Value("${jwt.expiration.time}")
    private long JWT_EXPIRATION_TIME;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyByte = JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        this.secretKey = new SecretKeySpec(keyByte,"HmacSHA256");
    }

    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimsFunction){
        return claimsFunction.apply(
                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
        );
    }

    private boolean isTokenExpired(String token){
        return extractClaims(token,Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (userDetails.getUsername().equals(username));
    }
}
