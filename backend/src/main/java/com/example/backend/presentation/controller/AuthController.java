package com.example.backend.presentation.controller;

import com.example.backend.core.service.AuthService;
import com.example.backend.presentation.dto.auth.AuthResponse;
import com.example.backend.presentation.dto.auth.LoginRequest;
import com.example.backend.presentation.dto.auth.RegisterRequest;
import com.example.backend.presentation.dto.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller quản lý xác thực: Đăng nhập và Đăng ký tài khoản (Công khai cho mọi người dùng).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * API Đăng ký tài khoản khách hàng mới.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API Đăng nhập và lấy JWT token.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
