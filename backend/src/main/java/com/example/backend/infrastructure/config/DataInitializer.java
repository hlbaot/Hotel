package com.example.backend.infrastructure.config;

import com.example.backend.core.entity.User;
import com.example.backend.core.enums.Role;
import com.example.backend.core.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Khởi tạo dữ liệu ban đầu cho hệ thống: Tự động tạo 1 tài khoản Admin mặc định khi chạy lần đầu.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Kiểm tra xem đã có tài khoản admin nào chưa
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Quản trị viên hệ thống");
            admin.setPhone("0901234567");
            admin.setCccd("012345678901");
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            System.out.println(">>> Đã khởi tạo tài khoản admin mặc định: admin / admin123");
        }
    }
}
