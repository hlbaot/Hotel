package com.example.backend.presentation.controller;

import com.example.backend.core.enums.RequestStatus;
import com.example.backend.core.service.RentalRequestService;
import com.example.backend.presentation.dto.request.RentalRequestCreate;
import com.example.backend.presentation.dto.request.RentalRequestResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý yêu cầu thuê phòng:
 * - Khách: Gửi yêu cầu thuê phòng, xem yêu cầu của mình
 * - Admin: Xem danh sách yêu cầu, duyệt (approve) hoặc từ chối (reject)
 */
@RestController
@RequestMapping("/api/rental-requests")
public class RentalRequestController {

    private final RentalRequestService rentalRequestService;

    public RentalRequestController(RentalRequestService rentalRequestService) {
        this.rentalRequestService = rentalRequestService;
    }

    /**
     * Lấy toàn bộ danh sách yêu cầu thuê phòng (chỉ ADMIN).
     * GET /api/rental-requests?status=PENDING
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RentalRequestResponse>> getAllRequests(@RequestParam(required = false) RequestStatus status) {
        if (status != null) {
            return ResponseEntity.ok(rentalRequestService.getRequestsByStatus(status));
        }
        return ResponseEntity.ok(rentalRequestService.getAllRequests());
    }

    /**
     * Khách hàng xem danh sách yêu cầu thuê phòng của chính mình.
     * GET /api/rental-requests/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<RentalRequestResponse>> getMyRequests(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(rentalRequestService.getMyRequests(username));
    }

    /**
     * Xem chi tiết yêu cầu thuê phòng theo ID.
     * GET /api/rental-requests/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RentalRequestResponse> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalRequestService.getRequestById(id));
    }

    /**
     * Khách gửi yêu cầu đăng ký thuê phòng.
     * POST /api/rental-requests
     */
    @PostMapping
    public ResponseEntity<RentalRequestResponse> createRequest(Authentication authentication,
                                                               @Valid @RequestBody RentalRequestCreate requestDto) {
        String username = authentication.getName();
        RentalRequestResponse response = rentalRequestService.createRequest(username, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Admin chấp thuận yêu cầu thuê phòng (chỉ ADMIN).
     * PUT /api/rental-requests/{id}/approve
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RentalRequestResponse> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(rentalRequestService.approveRequest(id));
    }

    /**
     * Admin từ chối yêu cầu thuê phòng (chỉ ADMIN).
     * PUT /api/rental-requests/{id}/reject
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RentalRequestResponse> rejectRequest(@PathVariable Long id) {
        return ResponseEntity.ok(rentalRequestService.rejectRequest(id));
    }
}
