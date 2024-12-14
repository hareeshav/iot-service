package com.relay42.iotservice.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JWTAuthentication extends AbstractAuthenticationToken {

    private final Claims claims;

    public JWTAuthentication(Claims claims) {
        super(null);
        this.claims = claims;
        setAuthenticated(true);
    }

    public Claims getClaims() {
        return claims;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return claims.getSubject();
    }
}

