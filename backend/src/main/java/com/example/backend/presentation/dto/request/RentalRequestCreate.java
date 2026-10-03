package com.example.backend.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public class RentalRequestCreate {

    @NotNull(message = "Phòng không được để trống")
    private Long roomId;

    private String message;

    public RentalRequestCreate() {
    }

    public RentalRequestCreate(Long roomId, String message) {
        this.roomId = roomId;
        this.message = message;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
