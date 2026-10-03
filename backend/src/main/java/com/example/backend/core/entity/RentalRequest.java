package com.example.backend.core.entity;

import com.example.backend.core.enums.RequestStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Thực thể RentalRequest: Quản lý các yêu cầu thuê phòng do khách gửi đến chủ trọ.
 */
@Entity
@Table(name = "rental_requests")
public class RentalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Khách hàng gửi yêu cầu thuê phòng */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    /** Phòng trọ mà khách mong muốn thuê */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /** Lời nhắn, ghi chú kèm theo từ khách (ví dụ: ngày dự kiến dọn vào, số người ở...) */
    @Column(columnDefinition = "TEXT")
    private String message;

    /** Trạng thái duyệt: PENDING (đang chờ), APPROVED (đã đồng ý), REJECTED (từ chối) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    /** Thời điểm khách gửi yêu cầu */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Thời điểm cập nhật trạng thái duyệt gần nhất */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public RentalRequest() {
    }

    public RentalRequest(User customer, Room room, String message, RequestStatus status) {
        this.customer = customer;
        this.room = room;
        this.message = message;
        this.status = status;
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

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getUser() {
        return customer;
    }

    public void setUser(User user) {
        this.customer = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
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
