package com.example.backend.core.entity;

import com.example.backend.core.enums.InvoiceStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể Invoice: Quản lý hóa đơn tiền phòng, tiền điện, tiền nước hàng tháng.
 */
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Hợp đồng thuê tương ứng với hóa đơn này */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /** Tháng xuất hóa đơn (1 - 12) */
    @Column(nullable = false)
    private Integer month;

    /** Năm xuất hóa đơn (ví dụ: 2026) */
    @Column(nullable = false)
    private Integer year;

    /** Số ký điện tiêu thụ trong tháng (kWh) */
    @Column(name = "electricity_number")
    private Integer electricityNumber;

    /** Số khối nước tiêu thụ trong tháng (m3) */
    @Column(name = "water_number")
    private Integer waterNumber;

    /** Tổng số tiền cần thanh toán = Tiền phòng + (số điện * đơn giá) + (số nước * đơn giá) */
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    /** Trạng thái thanh toán: UNPAID (chưa thanh toán) hoặc PAID (đã thanh toán) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    /** Thời gian lập hóa đơn */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Thời gian cập nhật hóa đơn gần nhất */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Invoice() {
    }

    public Invoice(Contract contract, Integer month, Integer year, Integer electricityNumber, Integer waterNumber, BigDecimal totalAmount, InvoiceStatus status) {
        this.contract = contract;
        this.month = month;
        this.year = year;
        this.electricityNumber = electricityNumber;
        this.waterNumber = waterNumber;
        this.totalAmount = totalAmount;
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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getElectricityNumber() {
        return electricityNumber;
    }

    public void setElectricityNumber(Integer electricityNumber) {
        this.electricityNumber = electricityNumber;
    }

    public Integer getWaterNumber() {
        return waterNumber;
    }

    public void setWaterNumber(Integer waterNumber) {
        this.waterNumber = waterNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
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
