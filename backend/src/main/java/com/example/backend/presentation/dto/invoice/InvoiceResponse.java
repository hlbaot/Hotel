package com.example.backend.presentation.dto.invoice;

import com.example.backend.core.entity.Invoice;
import com.example.backend.core.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private Long id;
    private Long contractId;
    private String roomNumber;
    private String customerName;
    private Integer month;
    private Integer year;
    private Integer electricityNumber;
    private Integer waterNumber;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDateTime createdAt;

    public InvoiceResponse() {
    }

    public static InvoiceResponse fromEntity(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        if (invoice.getContract() != null) {
            response.setContractId(invoice.getContract().getId());
            if (invoice.getContract().getRoom() != null) {
                response.setRoomNumber(invoice.getContract().getRoom().getRoomNumber());
            }
            if (invoice.getContract().getCustomer() != null) {
                response.setCustomerName(invoice.getContract().getCustomer().getFullName() != null 
                        ? invoice.getContract().getCustomer().getFullName() 
                        : invoice.getContract().getCustomer().getUsername());
            }
        }
        response.setMonth(invoice.getMonth());
        response.setYear(invoice.getYear());
        response.setElectricityNumber(invoice.getElectricityNumber());
        response.setWaterNumber(invoice.getWaterNumber());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus());
        response.setCreatedAt(invoice.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
}
