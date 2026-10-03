package com.example.backend.core.repository;

import com.example.backend.core.entity.Contract;
import com.example.backend.core.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Tầng truy xuất dữ liệu cho thực thể Contract.
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * Lấy toàn bộ hợp đồng của một khách hàng cụ thể.
     *
     * @param customerId ID của khách thuê
     * @return danh sách hợp đồng
     */
    List<Contract> findByCustomerId(Long customerId);

    /**
     * Lấy hợp đồng của khách hàng theo trạng thái (ví dụ: hợp đồng đang ACTIVE).
     *
     * @param customerId ID của khách thuê
     * @param status     trạng thái hợp đồng
     * @return danh sách hợp đồng phù hợp
     */
    List<Contract> findByCustomerIdAndStatus(Long customerId, ContractStatus status);

    /**
     * Tìm các hợp đồng gắn với một phòng theo trạng thái.
     *
     * @param roomId ID của phòng
     * @param status trạng thái hợp đồng
     * @return danh sách hợp đồng
     */
    List<Contract> findByRoomIdAndStatus(Long roomId, ContractStatus status);

    /**
     * Kiểm tra xem phòng có đang tồn tại hợp đồng hiệu lực hay không.
     * Dùng để thực thi quy tắc nghiệp vụ: Không xóa hoặc không cho thuê phòng đã có người ở.
     *
     * @param roomId ID của phòng cần kiểm tra
     * @param status trạng thái hợp đồng (thường là ACTIVE)
     * @return true nếu phòng đang có hợp đồng ACTIVE
     */
    boolean existsByRoomIdAndStatus(Long roomId, ContractStatus status);

    /**
     * Lấy hợp đồng đầu tiên gắn với một phòng theo trạng thái.
     *
     * @param roomId ID phòng
     * @param status trạng thái hợp đồng
     * @return Optional chứa hợp đồng nếu tìm thấy
     */
    Optional<Contract> findFirstByRoomIdAndStatus(Long roomId, ContractStatus status);
}
