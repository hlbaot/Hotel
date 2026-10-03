package com.example.backend.core.repository;

import com.example.backend.core.entity.User;
import com.example.backend.core.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Tầng truy xuất dữ liệu cho thực thể User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Tìm kiếm người dùng dựa trên tên đăng nhập.
     *
     * @param username tên đăng nhập cần tìm
     * @return Optional chứa User nếu tìm thấy, rỗng nếu không tồn tại
     */
    Optional<User> findByUsername(String username);

    /**
     * Kiểm tra xem tên đăng nhập đã được sử dụng hay chưa (dùng khi đăng ký tài khoản).
     *
     * @param username tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại false
     */
    boolean existsByUsername(String username);

    /**
     * Lấy danh sách người dùng theo vai trò (ví dụ: lấy tất cả khách thuê CUSTOMER).
     *
     * @param role vai trò cần lọc (ADMIN hoặc CUSTOMER)
     * @return danh sách người dùng tương ứng
     */
    List<User> findByRole(Role role);
}
