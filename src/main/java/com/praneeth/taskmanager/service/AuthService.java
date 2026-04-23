package com.praneeth.taskmanager.service;

import com.praneeth.taskmanager.dto.request.LoginRequest;
import com.praneeth.taskmanager.dto.request.RegisterRequest;
import com.praneeth.taskmanager.dto.response.AuthResponse;

/**
 * Interface defining authentication logic.
 */
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
