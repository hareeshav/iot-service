package com.relay42.iotservice.controller;

import com.relay42.iotservice.model.UserCredentials;
import com.relay42.iotservice.security.JWTTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private JWTTokenService jwtTokenService;

    @PostMapping("/api/login")
    public String login(@RequestBody UserCredentials credentials) {
        // In a real-world scenario, you'd authenticate the user by checking the database.
        // If credentials are valid, generate and return the JWT token.

        // For simplicity, assume that the credentials are valid and generate the token
        return jwtTokenService.generateToken(credentials.username());
    }
}
