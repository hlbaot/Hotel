package com.example.backend.core.repository;

import com.example.backend.core.entity.RentalRequest;
import com.example.backend.core.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Tầng truy xuất dữ liệu cho thực thể RentalRequest.
 */
@Repository
public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {

    /**
     * Lấy tất cả yêu cầu thuê phòng của một khách hàng cụ thể.
     *
     * @param customerId ID khách hàng
     * @return danh sách yêu cầu thuê
     */
    List<RentalRequest> findByCustomerId(Long customerId);

    /**
     * Lấy tất cả yêu cầu thuê được gửi cho một phòng cụ thể.
     *
     * @param roomId ID phòng trọ
     * @return danh sách yêu cầu thuê
     */
    List<RentalRequest> findByRoomId(Long roomId);

    /**
     * Lấy các yêu cầu thuê theo trạng thái (PENDING, APPROVED, REJECTED).
     *
     * @param status trạng thái duyệt
     * @return danh sách yêu cầu
     */
    List<RentalRequest> findByStatus(RequestStatus status);

    /**
     * Kiểm tra xem khách hàng đã có yêu cầu ở trạng thái chỉ định (thường là PENDING)
     * cho cùng một phòng hay chưa, nhằm tránh gửi trùng lặp nhiều lần.
     *
     * @param customerId ID khách hàng
     * @param roomId     ID phòng
     * @param status     trạng thái kiểm tra (PENDING)
     * @return true nếu đã có yêu cầu tương tự
     */
    boolean existsByCustomerIdAndRoomIdAndStatus(Long customerId, Long roomId, RequestStatus status);
}
