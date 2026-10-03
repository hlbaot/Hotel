package com.example.backend.core.repository;

import com.example.backend.core.entity.Invoice;
import com.example.backend.core.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Tầng truy xuất dữ liệu cho thực thể Invoice.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Lấy toàn bộ danh sách hóa đơn thuộc về một hợp đồng.
     *
     * @param contractId ID hợp đồng
     * @return danh sách hóa đơn
     */
    List<Invoice> findByContractId(Long contractId);

    /**
     * Lấy danh sách hóa đơn của một khách hàng cụ thể (thông qua hợp đồng của khách).
     *
     * @param customerId ID người dùng
     * @return danh sách hóa đơn cá nhân
     */
    List<Invoice> findByContractCustomerId(Long customerId);

    /**
     * Kiểm tra xem hợp đồng đã được xuất hóa đơn cho tháng/năm này chưa (tránh tạo trùng lặp hóa đơn).
     *
     * @param contractId ID hợp đồng
     * @param month      tháng lập hóa đơn
     * @param year       năm lập hóa đơn
     * @return true nếu đã có hóa đơn trong tháng/năm đó
     */
    boolean existsByContractIdAndMonthAndYear(Long contractId, Integer month, Integer year);

    /**
     * Tìm hóa đơn theo hợp đồng và tháng/năm cụ thể.
     *
     * @param contractId ID hợp đồng
     * @param month      tháng
     * @param year       năm
     * @return Optional chứa hóa đơn tương ứng
     */
    Optional<Invoice> findByContractIdAndMonthAndYear(Long contractId, Integer month, Integer year);

    /**
     * Lấy danh sách hóa đơn theo trạng thái (UNPAID hoặc PAID).
     *
     * @param status trạng thái thanh toán
     * @return danh sách hóa đơn
     */
    List<Invoice> findByStatus(InvoiceStatus status);

    /**
     * Tính tổng doanh thu từ các hóa đơn đã thanh toán.
     * Hỗ trợ lọc tùy chọn theo tháng và năm (nếu null sẽ tính lũy kế toàn bộ).
     *
     * @param status trạng thái hóa đơn (thường là PAID)
     * @param month  tháng cần thống kê (tùy chọn)
     * @param year   năm cần thống kê (tùy chọn)
     * @return tổng tiền doanh thu (nếu không có bản ghi nào sẽ trả về 0)
     */
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE " +
           "i.status = :status AND " +
           "(:month IS NULL OR i.month = :month) AND " +
           "(:year IS NULL OR i.year = :year)")
    BigDecimal calculateRevenue(@Param("status") InvoiceStatus status,
                                @Param("month") Integer month,
                                @Param("year") Integer year);
}
