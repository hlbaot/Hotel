package com.example.backend.core.repository;

import com.example.backend.core.entity.Room;
import com.example.backend.core.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Tầng truy xuất dữ liệu cho thực thể Room.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Tìm phòng trọ theo số phòng.
     *
     * @param roomNumber số phòng cần tìm (ví dụ: "101")
     * @return Optional chứa thông tin phòng
     */
    Optional<Room> findByRoomNumber(String roomNumber);

    /**
     * Kiểm tra số phòng đã tồn tại trong hệ thống chưa (tránh tạo trùng số phòng).
     *
     * @param roomNumber số phòng cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại false
     */
    boolean existsByRoomNumber(String roomNumber);

    /**
     * Lọc danh sách phòng theo trạng thái (ví dụ: lấy tất cả các phòng AVAILABLE còn trống).
     *
     * @param status trạng thái phòng cần lọc
     * @return danh sách phòng phù hợp
     */
    List<Room> findByStatus(RoomStatus status);

    /**
     * Đếm số lượng phòng theo trạng thái (phục vụ biểu đồ/báo cáo thống kê).
     *
     * @param status trạng thái phòng
     * @return số lượng phòng ở trạng thái đó
     */
    long countByStatus(RoomStatus status);

    /**
     * Lọc phòng nâng cao theo cả trạng thái và khoảng giá mong muốn.
     * Các tham số là tùy chọn (nếu truyền null sẽ bỏ qua điều kiện đó).
     *
     * @param status   trạng thái phòng (có thể null)
     * @param minPrice giá tối thiểu (có thể null)
     * @param maxPrice giá tối đa (có thể null)
     * @return danh sách phòng thỏa mãn các điều kiện
     */
    @Query("SELECT r FROM Room r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:minPrice IS NULL OR r.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR r.price <= :maxPrice)")
    List<Room> filterRooms(@Param("status") RoomStatus status,
                           @Param("minPrice") BigDecimal minPrice,
                           @Param("maxPrice") BigDecimal maxPrice);
}
