package com.praneeth.taskmanager.service.impl;

import com.praneeth.taskmanager.dto.request.LoginRequest;
import com.praneeth.taskmanager.dto.request.RegisterRequest;
import com.praneeth.taskmanager.dto.response.AuthResponse;
import com.praneeth.taskmanager.exception.DuplicateEmailException;
import com.praneeth.taskmanager.model.User;
import com.praneeth.taskmanager.repository.UserRepository;
import com.praneeth.taskmanager.service.AuthService;
import com.praneeth.taskmanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuthService handling business logic for authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed. Email already exists: {}", request.getEmail());
            throw new DuplicateEmailException("Email is already registered.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        log.info("User registered successfully with email: {}", user.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtUtil.generateToken(userDetails);

        log.info("User logged in successfully: {}", request.getEmail());
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
