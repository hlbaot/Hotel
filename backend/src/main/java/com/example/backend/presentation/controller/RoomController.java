package com.example.backend.presentation.controller;

import com.example.backend.core.enums.RoomStatus;
import com.example.backend.core.service.RoomService;
import com.example.backend.presentation.dto.room.RoomRequest;
import com.example.backend.presentation.dto.room.RoomResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller quản lý phòng trọ:
 * - Xem: Công khai cho mọi người
 * - Thêm, Sửa, Xóa: Dành riêng cho ADMIN
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Lấy danh sách phòng hoặc lọc phòng theo trạng thái và khoảng giá.
     * GET /api/rooms?status=AVAILABLE&minPrice=1000000&maxPrice=3000000
     */
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        if (status != null || minPrice != null || maxPrice != null) {
            return ResponseEntity.ok(roomService.filterRooms(status, minPrice, maxPrice));
        }
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    /**
     * Xem chi tiết một phòng theo ID.
     * GET /api/rooms/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    /**
     * Thêm phòng trọ mới (chỉ ADMIN).
     * POST /api/rooms
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    /**
     * Cập nhật thông tin phòng trọ (chỉ ADMIN).
     * PUT /api/rooms/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    /**
     * Xóa phòng trọ (chỉ ADMIN, kiểm tra không xóa phòng đang thuê).
     * DELETE /api/rooms/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
