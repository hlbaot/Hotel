package com.example.backend.presentation.controller;

import com.example.backend.core.service.UserService;
import com.example.backend.presentation.dto.user.ChangePasswordRequest;
import com.example.backend.presentation.dto.user.UpdateProfileRequest;
import com.example.backend.presentation.dto.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý người dùng:
 * - Khách: Xem và cập nhật hồ sơ cá nhân, đổi mật khẩu (/api/me)
 * - ADMIN: Quản lý danh sách người dùng và khách thuê (/api/users/**)
 */
@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Khách xem thông tin hồ sơ của chính mình.
     * GET /api/me
     */
    @GetMapping("/api/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    /**
     * Khách cập nhật thông tin hồ sơ của chính mình.
     * PUT /api/me
     */
    @PutMapping("/api/me")
    public ResponseEntity<UserResponse> updateMyProfile(Authentication authentication,
                                                       @Valid @RequestBody UpdateProfileRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateProfile(username, request));
    }

    /**
     * Đổi mật khẩu tài khoản hiện tại.
     * POST /api/me/change-password
     */
    @PostMapping("/api/me/change-password")
    public ResponseEntity<String> changePassword(Authentication authentication,
                                                 @Valid @RequestBody ChangePasswordRequest request) {
        String username = authentication.getName();
        userService.changePassword(username, request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }

    /**
     * Lấy toàn bộ danh sách người dùng (chỉ ADMIN).
     * GET /api/users
     */
    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Lấy danh sách khách thuê trọ (chỉ ADMIN).
     * GET /api/users/customers
     */
    @GetMapping("/api/users/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        return ResponseEntity.ok(userService.getAllCustomers());
    }

    /**
     * Lấy chi tiết thông tin người dùng theo ID (chỉ ADMIN).
     * GET /api/users/{id}
     */
    @GetMapping("/api/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
