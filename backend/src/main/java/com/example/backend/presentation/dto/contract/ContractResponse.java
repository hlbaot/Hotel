package com.example.backend.presentation.dto.contract;

import com.example.backend.core.entity.Contract;
import com.example.backend.core.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContractResponse {

    private Long id;
    private Long roomId;
    private String roomNumber;
    private Long customerId;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal deposit;
    private ContractStatus status;
    private LocalDateTime createdAt;

    public ContractResponse() {
    }

    public static ContractResponse fromEntity(Contract contract) {
        ContractResponse response = new ContractResponse();
        response.setId(contract.getId());
        if (contract.getRoom() != null) {
            response.setRoomId(contract.getRoom().getId());
            response.setRoomNumber(contract.getRoom().getRoomNumber());
        }
        if (contract.getCustomer() != null) {
            response.setCustomerId(contract.getCustomer().getId());
            response.setCustomerName(contract.getCustomer().getFullName() != null ? contract.getCustomer().getFullName() : contract.getCustomer().getUsername());
        }
        response.setStartDate(contract.getStartDate());
        response.setEndDate(contract.getEndDate());
        response.setDeposit(contract.getDeposit());
        response.setStatus(contract.getStatus());
        response.setCreatedAt(contract.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
}
