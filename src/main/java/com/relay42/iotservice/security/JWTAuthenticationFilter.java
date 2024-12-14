package com.relay42.iotservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "your-secret-key";  // Replace with the secret key

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the JWT token from the "Authorization" header
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            // Remove "Bearer " prefix and extract the token
            token = token.substring(7);

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();

                Authentication authentication = new JWTAuthentication(claims);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Handle the exception (e.g., token expired, invalid signature, etc.)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
