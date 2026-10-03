package com.example.backend.core.service;

import com.example.backend.core.entity.Contract;
import com.example.backend.core.entity.Room;
import com.example.backend.core.entity.User;
import com.example.backend.core.enums.ContractStatus;
import com.example.backend.core.enums.RoomStatus;
import com.example.backend.core.repository.ContractRepository;
import com.example.backend.core.repository.RoomRepository;
import com.example.backend.core.repository.UserRepository;
import com.example.backend.presentation.dto.contract.ContractRequest;
import com.example.backend.presentation.dto.contract.ContractResponse;
import com.example.backend.presentation.exception.BusinessException;
import com.example.backend.presentation.exception.ResourceNotFoundException;
import com.example.backend.presentation.exception.RoomNotAvailableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý hợp đồng thuê phòng: Tạo mới, kết thúc hợp đồng và tự động đồng bộ trạng thái phòng.
 */
@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ContractService(ContractRepository contractRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.contractRepository = contractRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lấy danh sách tất cả hợp đồng (dành cho Admin quản lý).
     */
    public List<ContractResponse> getAllContracts() {
        return contractRepository.findAll().stream()
                .map(ContractResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách hợp đồng của một khách hàng theo customerId.
     */
    public List<ContractResponse> getContractsByCustomerId(Long customerId) {
        return contractRepository.findByCustomerId(customerId).stream()
                .map(ContractResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách hợp đồng của chính khách hàng đang đăng nhập theo username (/api/contracts/my).
     */
    public List<ContractResponse> getMyContracts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));
        return getContractsByCustomerId(user.getId());
    }

    /**
     * Lấy chi tiết hợp đồng theo ID.
     */
    public ContractResponse getContractById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng với ID: " + id));
        return ContractResponse.fromEntity(contract);
    }

    /**
     * Tạo hợp đồng thuê phòng mới.
     * Quy tắc nghiệp vụ:
     * - Kiểm tra phòng có đang bị thuê (OCCUPIED hoặc có hợp đồng ACTIVE) hay không.
     * - Chuyển trạng thái phòng sang OCCUPIED ngay khi hợp đồng có hiệu lực trong cùng 1 Transaction (@Transactional).
     *
     * @param request dữ liệu hợp đồng cần tạo
     * @throws RoomNotAvailableException nếu phòng đã có người thuê
     */
    @Transactional
    public ContractResponse createContract(ContractRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + request.getRoomId()));

        // Kiểm tra phòng có sẵn sàng để thuê hay không
        if (room.getStatus() == RoomStatus.OCCUPIED || contractRepository.existsByRoomIdAndStatus(room.getId(), ContractStatus.ACTIVE)) {
            throw new RoomNotAvailableException("Phòng " + room.getRoomNumber() + " hiện đã có người thuê");
        }

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + request.getCustomerId()));

        Contract contract = new Contract();
        contract.setRoom(room);
        contract.setCustomer(customer);
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setDeposit(request.getDeposit());
        contract.setStatus(request.getStatus() != null ? request.getStatus() : ContractStatus.ACTIVE);

        // Đổi trạng thái phòng sang OCCUPIED khi hợp đồng có hiệu lực
        if (contract.getStatus() == ContractStatus.ACTIVE) {
            room.setStatus(RoomStatus.OCCUPIED);
            roomRepository.save(room);
        }

        Contract savedContract = contractRepository.save(contract);
        return ContractResponse.fromEntity(savedContract);
    }

    /**
     * Kết thúc / thanh lý hợp đồng thuê phòng.
     * Quy tắc nghiệp vụ:
     * - Chuyển trạng thái hợp đồng thành ENDED.
     * - Tự động giải phóng trạng thái phòng về AVAILABLE để sẵn sàng cho khách sau thuê.
     *
     * @param id ID của hợp đồng cần kết thúc
     * @throws BusinessException nếu hợp đồng đã kết thúc trước đó
     */
    @Transactional
    public ContractResponse endContract(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng với ID: " + id));

        if (contract.getStatus() == ContractStatus.ENDED) {
            throw new BusinessException("Hợp đồng này đã kết thúc trước đó");
        }

        contract.setStatus(ContractStatus.ENDED);
        Contract updatedContract = contractRepository.save(contract);

        // Trả trạng thái phòng về AVAILABLE
        Room room = contract.getRoom();
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        return ContractResponse.fromEntity(updatedContract);
    }
}
