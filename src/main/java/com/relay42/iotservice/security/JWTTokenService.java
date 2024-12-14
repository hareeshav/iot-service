package com.relay42.iotservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class JWTTokenService {

    private static final String SECRET_KEY = "your-secret-key";  // Replace with a more secure key

    // Generate JWT token for a user
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // Subject could be the username or user ID
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10 hours validity
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}

