package com.plagiarism.engine.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final String adminKey;
    private final String reviewerKey;

    public ApiKeyAuthenticationFilter(String adminKey, String reviewerKey) {
        this.adminKey = adminKey;
        this.reviewerKey = reviewerKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey != null) {
            if (apiKey.equals(adminKey)) {
                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"), 
                        new SimpleGrantedAuthority("ROLE_REVIEWER")
                );
                var auth = new UsernamePasswordAuthenticationToken("Admin", null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else if (apiKey.equals(reviewerKey)) {
                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_REVIEWER")
                );
                var auth = new UsernamePasswordAuthenticationToken("Reviewer", null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
