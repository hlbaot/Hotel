package com.example.backend.core.service;

import com.example.backend.core.enums.InvoiceStatus;
import com.example.backend.core.enums.RoomStatus;
import com.example.backend.core.repository.InvoiceRepository;
import com.example.backend.core.repository.RoomRepository;
import com.example.backend.presentation.dto.stats.RevenueStatResponse;
import com.example.backend.presentation.dto.stats.RoomStatResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service phục vụ báo cáo và thống kê (doanh thu, tỷ lệ lấp đầy phòng) dành cho Admin.
 */
@Service
public class StatisticService {

    private final InvoiceRepository invoiceRepository;
    private final RoomRepository roomRepository;

    public StatisticService(InvoiceRepository invoiceRepository, RoomRepository roomRepository) {
        this.invoiceRepository = invoiceRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Thống kê doanh thu thực nhận từ các hóa đơn đã thanh toán (PAID).
     * Có thể lọc theo tháng và năm hoặc xem tổng lũy kế toàn bộ.
     *
     * @param month tháng cần thống kê (tùy chọn, null nếu tính cả năm)
     * @param year  năm cần thống kê (tùy chọn)
     * @return RevenueStatResponse chứa tổng số tiền và kỳ báo cáo
     */
    public RevenueStatResponse getRevenue(Integer month, Integer year) {
        BigDecimal revenue = invoiceRepository.calculateRevenue(InvoiceStatus.PAID, month, year);
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        return new RevenueStatResponse(revenue, month, year);
    }

    /**
     * Thống kê số lượng phòng theo từng trạng thái:
     * - Tổng số phòng
     * - Số phòng còn trống (AVAILABLE)
     * - Số phòng đang có người thuê (OCCUPIED)
     * - Số phòng đang bảo trì / sửa chữa (MAINTENANCE)
     *
     * @return RoomStatResponse chứa các chỉ số đếm phòng
     */
    public RoomStatResponse getRoomStatistics() {
        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.countByStatus(RoomStatus.AVAILABLE);
        long occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED);
        long maintenanceRooms = roomRepository.countByStatus(RoomStatus.MAINTENANCE);

        return new RoomStatResponse(totalRooms, availableRooms, occupiedRooms, maintenanceRooms);
    }
}
