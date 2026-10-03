package com.example.backend.core.entity;

import com.example.backend.core.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Thực thể User: Quản lý thông tin tài khoản người dùng và thông tin cá nhân khách thuê.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên đăng nhập (duy nhất trong toàn hệ thống) */
    @Column(nullable = false, unique = true)
    private String username;

    /** Mật khẩu đã được mã hóa bằng BCrypt */
    @Column(nullable = false)
    private String password;

    /** Họ và tên đầy đủ */
    @Column(name = "full_name")
    private String fullName;

    /** Số điện thoại liên hệ */
    private String phone;

    /** Số Căn cước công dân / CMND */
    private String cccd;

    /** Vai trò: ADMIN (chủ trọ) hoặc CUSTOMER (khách thuê) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Thời gian tạo tài khoản */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Thời gian cập nhật thông tin gần nhất */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String username, String password, String fullName, String phone, String cccd, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.cccd = cccd;
        this.role = role;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
