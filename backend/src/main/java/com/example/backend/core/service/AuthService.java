package com.example.backend.core.service;

import com.example.backend.core.entity.User;
import com.example.backend.core.enums.Role;
import com.example.backend.core.repository.UserRepository;
import com.example.backend.infrastructure.security.JwtUtil;
import com.example.backend.presentation.dto.auth.AuthResponse;
import com.example.backend.presentation.dto.auth.LoginRequest;
import com.example.backend.presentation.dto.auth.RegisterRequest;
import com.example.backend.presentation.dto.user.UserResponse;
import com.example.backend.presentation.exception.BusinessException;
import com.example.backend.presentation.exception.DuplicateResourceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service xử lý xác thực người dùng: Đăng ký tài khoản, đăng nhập và cấp phát JWT token.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Xác thực thông tin đăng nhập và tạo token JWT.
     *
     * @param request chứa tên đăng nhập và mật khẩu chưa mã hóa
     * @return AuthResponse chứa chuỗi token JWT, tên đăng nhập và vai trò
     * @throws BusinessException nếu tài khoản không tồn tại hoặc mật khẩu sai
     */
    public AuthResponse login(LoginRequest request) {
        // Tìm tài khoản theo username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Tài khoản hoặc mật khẩu không chính xác"));

        // Kiểm tra khớp mật khẩu với bản hash trong database
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Tài khoản hoặc mật khẩu không chính xác");
        }

        // Tạo JWT token chứa thông tin username và role
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    /**
     * Đăng ký tài khoản khách hàng mới vào hệ thống.
     * Mặc định tài khoản đăng ký mới sẽ mang vai trò CUSTOMER.
     *
     * @param request thông tin đăng ký (username, password, họ tên, sđt, cccd)
     * @return thông tin tài khoản vừa tạo (không bao gồm mật khẩu)
     * @throws DuplicateResourceException nếu username đã tồn tại
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Kiểm tra trùng username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập '" + request.getUsername() + "' đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        // Mã hóa mật khẩu an toàn với BCrypt trước khi lưu vào DB
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setCccd(request.getCccd());
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }
}
