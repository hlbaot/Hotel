package com.example.backend.core.service;

import com.example.backend.core.entity.RentalRequest;
import com.example.backend.core.entity.Room;
import com.example.backend.core.entity.User;
import com.example.backend.core.enums.RequestStatus;
import com.example.backend.core.enums.RoomStatus;
import com.example.backend.core.repository.RentalRequestRepository;
import com.example.backend.core.repository.RoomRepository;
import com.example.backend.core.repository.UserRepository;
import com.example.backend.presentation.dto.request.RentalRequestCreate;
import com.example.backend.presentation.dto.request.RentalRequestResponse;
import com.example.backend.presentation.exception.BusinessException;
import com.example.backend.presentation.exception.DuplicateResourceException;
import com.example.backend.presentation.exception.ResourceNotFoundException;
import com.example.backend.presentation.exception.RoomNotAvailableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý yêu cầu thuê phòng từ khách hàng: Gửi yêu cầu, duyệt (approve) hoặc từ chối (reject).
 */
@Service
public class RentalRequestService {

    private final RentalRequestRepository rentalRequestRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public RentalRequestService(RentalRequestRepository rentalRequestRepository,
                                RoomRepository roomRepository,
                                UserRepository userRepository) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lấy danh sách toàn bộ yêu cầu thuê phòng (dành cho Admin).
     */
    public List<RentalRequestResponse> getAllRequests() {
        return rentalRequestRepository.findAll().stream()
                .map(RentalRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lọc yêu cầu thuê theo trạng thái (ví dụ: lấy tất cả các yêu cầu đang PENDING).
     */
    public List<RentalRequestResponse> getRequestsByStatus(RequestStatus status) {
        return rentalRequestRepository.findByStatus(status).stream()
                .map(RentalRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách các yêu cầu của chính khách hàng đang đăng nhập theo username.
     */
    public List<RentalRequestResponse> getMyRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));
        return rentalRequestRepository.findByCustomerId(user.getId()).stream()
                .map(RentalRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết một yêu cầu thuê phòng theo ID.
     */
    public RentalRequestResponse getRequestById(Long id) {
        RentalRequest request = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu thuê phòng với ID: " + id));
        return RentalRequestResponse.fromEntity(request);
    }

    /**
     * Khách hàng gửi yêu cầu thuê phòng.
     * Quy tắc nghiệp vụ:
     * - Kiểm tra phòng có đang bị thuê (OCCUPIED) hay không.
     * - Kiểm tra khách hàng đã có yêu cầu PENDING cho phòng này chưa (tránh spam).
     *
     * @param username   tài khoản khách đang đăng nhập
     * @param requestDto thông tin phòng và lời nhắn
     */
    @Transactional
    public RentalRequestResponse createRequest(String username, RentalRequestCreate requestDto) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));

        Room room = roomRepository.findById(requestDto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + requestDto.getRoomId()));

        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new RoomNotAvailableException("Phòng " + room.getRoomNumber() + " hiện đã có người thuê");
        }

        if (rentalRequestRepository.existsByCustomerIdAndRoomIdAndStatus(customer.getId(), room.getId(), RequestStatus.PENDING)) {
            throw new DuplicateResourceException("Bạn đã gửi yêu cầu thuê phòng này và đang chờ duyệt");
        }

        RentalRequest request = new RentalRequest();
        request.setCustomer(customer);
        request.setRoom(room);
        request.setMessage(requestDto.getMessage());
        request.setStatus(RequestStatus.PENDING);

        RentalRequest savedRequest = rentalRequestRepository.save(request);
        return RentalRequestResponse.fromEntity(savedRequest);
    }

    /**
     * Admin chấp thuận yêu cầu thuê phòng (chuyển trạng thái sang APPROVED).
     *
     * @param id ID của yêu cầu
     * @throws BusinessException nếu yêu cầu đã được duyệt hoặc từ chối trước đó
     */
    @Transactional
    public RentalRequestResponse approveRequest(Long id) {
        RentalRequest request = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với ID: " + id));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException("Yêu cầu này đã được xử lý trước đó (Trạng thái: " + request.getStatus() + ")");
        }

        request.setStatus(RequestStatus.APPROVED);
        RentalRequest updatedRequest = rentalRequestRepository.save(request);
        return RentalRequestResponse.fromEntity(updatedRequest);
    }

    /**
     * Admin từ chối yêu cầu thuê phòng (chuyển trạng thái sang REJECTED).
     *
     * @param id ID của yêu cầu
     * @throws BusinessException nếu yêu cầu đã được duyệt hoặc từ chối trước đó
     */
    @Transactional
    public RentalRequestResponse rejectRequest(Long id) {
        RentalRequest request = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với ID: " + id));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException("Yêu cầu này đã được xử lý trước đó (Trạng thái: " + request.getStatus() + ")");
        }

        request.setStatus(RequestStatus.REJECTED);
        RentalRequest updatedRequest = rentalRequestRepository.save(request);
        return RentalRequestResponse.fromEntity(updatedRequest);
    }
}
