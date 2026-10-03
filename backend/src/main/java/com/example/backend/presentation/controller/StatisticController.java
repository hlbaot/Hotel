package com.example.backend.presentation.controller;

import com.example.backend.core.service.StatisticService;
import com.example.backend.presentation.dto.stats.RevenueStatResponse;
import com.example.backend.presentation.dto.stats.RoomStatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller thống kê doanh thu và tình trạng phòng (Dành riêng cho ADMIN).
 */
@RestController
@RequestMapping("/api/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    /**
     * Thống kê tổng doanh thu (các hóa đơn đã thanh toán).
     * GET /api/admin/statistics/revenue?month=10&year=2026
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueStatResponse> getRevenue(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statisticService.getRevenue(month, year));
    }

    /**
     * Thống kê tổng quan số lượng phòng theo trạng thái (AVAILABLE, OCCUPIED, MAINTENANCE).
     * GET /api/admin/statistics/rooms
     */
    @GetMapping("/rooms")
    public ResponseEntity<RoomStatResponse> getRoomStatistics() {
        return ResponseEntity.ok(statisticService.getRoomStatistics());
    }
}
