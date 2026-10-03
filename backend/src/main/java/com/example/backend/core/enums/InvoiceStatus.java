package com.example.backend.core.enums;

/**
 * Trạng thái thanh toán của hóa đơn hàng tháng.
 */
public enum InvoiceStatus {
    /**
     * Chưa thanh toán: hóa đơn vừa tạo và gửi cho khách đóng tiền.
     */
    UNPAID,

    /**
     * Đã thanh toán: khách đã thanh toán đầy đủ tiền phòng, điện, nước (dùng để tính doanh thu).
     */
    PAID
}
