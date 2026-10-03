package com.example.backend.core.entity;

import com.example.backend.core.enums.ContractStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Thực thể Contract: Quản lý hợp đồng thuê giữa chủ trọ và khách thuê.
 */
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Phòng được thuê trong hợp đồng */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /** Khách hàng đứng tên hợp đồng thuê */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    /** Ngày bắt đầu có hiệu lực của hợp đồng */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Ngày kết thúc hợp đồng */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /** Số tiền đặt cọc giữ phòng (VND) */
    @Column(nullable = false)
    private BigDecimal deposit;

    /** Trạng thái hợp đồng: ACTIVE (đang thuê) hoặc ENDED (đã kết thúc/thanh lý) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    /** Thời gian lập hợp đồng */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Thời gian cập nhật hợp đồng gần nhất */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Contract() {
    }

    public Contract(Room room, User customer, LocalDate startDate, LocalDate endDate, BigDecimal deposit, ContractStatus status) {
        this.room = room;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
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
