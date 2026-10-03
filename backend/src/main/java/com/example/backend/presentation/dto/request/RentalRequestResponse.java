package com.example.backend.presentation.dto.request;

import com.example.backend.core.entity.RentalRequest;
import com.example.backend.core.enums.RequestStatus;

import java.time.LocalDateTime;

public class RentalRequestResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long roomId;
    private String roomNumber;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdAt;

    public RentalRequestResponse() {
    }

    public static RentalRequestResponse fromEntity(RentalRequest request) {
        RentalRequestResponse response = new RentalRequestResponse();
        response.setId(request.getId());
        if (request.getCustomer() != null) {
            response.setCustomerId(request.getCustomer().getId());
            response.setCustomerName(request.getCustomer().getFullName() != null 
                    ? request.getCustomer().getFullName() 
                    : request.getCustomer().getUsername());
            response.setCustomerPhone(request.getCustomer().getPhone());
        }
        if (request.getRoom() != null) {
            response.setRoomId(request.getRoom().getId());
            response.setRoomNumber(request.getRoom().getRoomNumber());
        }
        response.setMessage(request.getMessage());
        response.setStatus(request.getStatus());
        response.setCreatedAt(request.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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
}
