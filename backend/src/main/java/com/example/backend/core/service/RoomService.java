package com.example.backend.core.service;

import com.example.backend.core.entity.Room;
import com.example.backend.core.enums.ContractStatus;
import com.example.backend.core.enums.RoomStatus;
import com.example.backend.core.repository.ContractRepository;
import com.example.backend.core.repository.RoomRepository;
import com.example.backend.presentation.dto.room.RoomRequest;
import com.example.backend.presentation.dto.room.RoomResponse;
import com.example.backend.presentation.exception.BusinessException;
import com.example.backend.presentation.exception.DuplicateResourceException;
import com.example.backend.presentation.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý phòng trọ: CRUD phòng, tìm kiếm/lọc phòng và kiểm soát quy tắc nghiệp vụ khi xóa phòng.
 */
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;

    public RoomService(RoomRepository roomRepository, ContractRepository contractRepository) {
        this.roomRepository = roomRepository;
        this.contractRepository = contractRepository;
    }

    /**
     * Lấy toàn bộ danh sách phòng trọ trong hệ thống.
     */
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lọc danh sách phòng theo trạng thái và khoảng giá (min - max).
     *
     * @param status   trạng thái phòng (AVAILABLE, OCCUPIED, MAINTENANCE)
     * @param minPrice mức giá tối thiểu
     * @param maxPrice mức giá tối đa
     */
    public List<RoomResponse> filterRooms(RoomStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        return roomRepository.filterRooms(status, minPrice, maxPrice).stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết một phòng theo ID.
     */
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
        return RoomResponse.fromEntity(room);
    }

    /**
     * Tạo mới một phòng trọ.
     * Kiểm tra số phòng không được trùng với phòng đã có.
     *
     * @param request dữ liệu phòng mới
     * @throws DuplicateResourceException nếu số phòng đã tồn tại
     */
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("Số phòng '" + request.getRoomNumber() + "' đã tồn tại");
        }

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setArea(request.getArea());
        room.setDescription(request.getDescription());
        room.setStatus(request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE);

        Room savedRoom = roomRepository.save(room);
        return RoomResponse.fromEntity(savedRoom);
    }

    /**
     * Cập nhật thông tin phòng trọ.
     *
     * @param id      ID của phòng cần cập nhật
     * @param request thông tin mới
     */
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));

        // Nếu thay đổi số phòng thì phải đảm bảo số phòng mới chưa bị dùng bởi phòng khác
        if (!room.getRoomNumber().equals(request.getRoomNumber()) &&
                roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("Số phòng '" + request.getRoomNumber() + "' đã tồn tại");
        }

        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setArea(request.getArea());
        room.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }

        Room updatedRoom = roomRepository.save(room);
        return RoomResponse.fromEntity(updatedRoom);
    }

    /**
     * Xóa phòng trọ khỏi hệ thống.
     * Quy tắc nghiệp vụ bắt buộc: KHÔNG xóa phòng đang có hợp đồng thuê hiệu lực (ACTIVE) hoặc đang OCCUPIED.
     *
     * @param id ID của phòng cần xóa
     * @throws BusinessException nếu phòng đang được thuê
     */
    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));

        boolean isOccupied = contractRepository.existsByRoomIdAndStatus(id, ContractStatus.ACTIVE);
        if (isOccupied || room.getStatus() == RoomStatus.OCCUPIED) {
            throw new BusinessException("Không thể xóa phòng đang có hợp đồng thuê hoặc đang có người ở");
        }

        roomRepository.delete(room);
    }
}
