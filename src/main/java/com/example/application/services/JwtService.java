package com.example.application.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JwtService {

    private static final String SECRET_KEY = "m9zcicAH7GvK1gLCeYeQjHSY7d35d8IX";
    private static final Duration EXPIRATION_TIME = Duration.ofDays(7);



    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }


}
