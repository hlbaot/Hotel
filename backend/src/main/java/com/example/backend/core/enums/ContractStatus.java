package com.example.backend.core.enums;

/**
 * Trạng thái của hợp đồng thuê phòng.
 */
public enum ContractStatus {
    /**
     * Hợp đồng đang có hiệu lực: khách đang thuê, phòng gắn liền ở trạng thái OCCUPIED.
     */
    ACTIVE,

    /**
     * Hợp đồng đã kết thúc: trả phòng hoặc hết hạn, phòng được giải phóng về trạng thái AVAILABLE.
     */
    ENDED
}
