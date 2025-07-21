package com.tossupflash.backend;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("DEBUG: Successfully extracted username: " + username);
            } catch (Exception e) {
                System.out.println("DEBUG: Failed to extract username from JWT: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(jwt, username)) {
                    System.out.println("DEBUG: JWT token validation successful for user: " + username);
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // Store user ID in request for later use
                    Long userId = jwtUtil.extractUserId(jwt);
                    System.out.println("DEBUG: Setting userId in request: " + userId);
                    request.setAttribute("userId", userId);
                } else {
                    System.out.println("DEBUG: JWT token validation failed for user: " + username);
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Exception during JWT validation: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 