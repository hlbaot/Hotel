package com.example.backend.core.enums;

/**
 * Trạng thái yêu cầu thuê phòng từ khách hàng gửi đến chủ trọ.
 */
public enum RequestStatus {
    /**
     * Đang chờ duyệt: khách vừa gửi yêu cầu và đang đợi chủ trọ phản hồi.
     */
    PENDING,

    /**
     * Đã chấp thuận: chủ trọ đồng ý cho thuê (chuẩn bị lập hợp đồng).
     */
    APPROVED,

    /**
     * Bị từ chối: chủ trọ từ chối yêu cầu (do phòng đã kín hoặc lý do khác).
     */
    REJECTED
}
