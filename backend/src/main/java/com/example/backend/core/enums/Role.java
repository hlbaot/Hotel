package com.example.backend.core.enums;

/**
 * Phân quyền người dùng trong hệ thống.
 */
public enum Role {
    /**
     * Quản trị viên (chủ trọ): toàn quyền quản lý phòng, hợp đồng, hóa đơn, người dùng và xem thống kê.
     */
    ADMIN,

    /**
     * Khách thuê trọ: có thể xem danh sách phòng, gửi yêu cầu thuê, xem hợp đồng và hóa đơn cá nhân.
     */
    CUSTOMER
}
