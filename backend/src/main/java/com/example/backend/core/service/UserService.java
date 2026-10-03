package com.example.backend.core.service;

import com.example.backend.core.entity.User;
import com.example.backend.core.enums.Role;
import com.example.backend.core.repository.UserRepository;
import com.example.backend.presentation.dto.user.ChangePasswordRequest;
import com.example.backend.presentation.dto.user.UpdateProfileRequest;
import com.example.backend.presentation.dto.user.UserResponse;
import com.example.backend.presentation.exception.BusinessException;
import com.example.backend.presentation.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý thông tin người dùng: Xem danh sách, cập nhật hồ sơ cá nhân và đổi mật khẩu.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Lấy toàn bộ danh sách người dùng trong hệ thống (dành cho Admin).
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách những người dùng có vai trò là khách thuê (CUSTOMER).
     */
    public List<UserResponse> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết một người dùng theo ID.
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        return UserResponse.fromEntity(user);
    }

    /**
     * Lấy thông tin tài khoản người dùng theo username (dành cho API /api/me).
     */
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với username: " + username));
        return UserResponse.fromEntity(user);
    }

    /**
     * Cập nhật thông tin hồ sơ cá nhân (họ tên, SĐT, CCCD).
     *
     * @param username username của người dùng đang đăng nhập
     * @param request  dữ liệu thông tin mới
     * @return thông tin sau khi cập nhật
     */
    @Transactional
    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với username: " + username));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setCccd(request.getCccd());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    /**
     * Đổi mật khẩu tài khoản người dùng.
     *
     * @param username username của người dùng cần đổi mật khẩu
     * @param request  chứa mật khẩu cũ và mật khẩu mới
     * @throws BusinessException nếu mật khẩu cũ cung cấp không trùng khớp
     */
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với username: " + username));

        // Kiểm tra tính hợp lệ của mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không chính xác");
        }

        // Mã hóa mật khẩu mới và lưu lại
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
