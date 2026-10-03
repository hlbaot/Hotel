package com.example.backend.core.enums;

/**
 * Trạng thái hiện tại của phòng trọ.
 */
public enum RoomStatus {
    /**
     * Phòng trống: sẵn sàng cho khách xem và thuê.
     */
    AVAILABLE,

    /**
     * Phòng đang có người thuê: đã có hợp đồng hiệu lực, không thể xóa hoặc cho người khác thuê.
     */
    OCCUPIED,

    /**
     * Phòng đang sửa chữa/bảo trì: tạm thời không nhận khách thuê mới.
     */
    MAINTENANCE
}
