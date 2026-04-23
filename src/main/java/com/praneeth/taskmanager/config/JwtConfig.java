package com.praneeth.taskmanager.config;

import com.praneeth.taskmanager.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter for intercepting requests and setting Security Context if JWT is valid.
 * Kept as JwtConfig as requested by the exact package structure.
 */
@Component
@RequiredArgsConstructor
public class JwtConfig extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final com.praneeth.taskmanager.repository.UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            userEmail = jwtUtil.extractUsername(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                com.praneeth.taskmanager.model.User user = userRepository.findByEmail(userEmail).orElse(null);
                if (user != null) {
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            user.getEmail(), user.getPassword(), Collections.emptyList());
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // Let it proceed to throw standard unauthorized if required endpoints
            logger.error("Failed to parse JWT", e);
        }

        filterChain.doFilter(request, response);
    }
}
