package com.example.backend.core.service;

import com.example.backend.core.entity.Contract;
import com.example.backend.core.entity.Invoice;
import com.example.backend.core.entity.User;
import com.example.backend.core.enums.InvoiceStatus;
import com.example.backend.core.repository.ContractRepository;
import com.example.backend.core.repository.InvoiceRepository;
import com.example.backend.core.repository.UserRepository;
import com.example.backend.infrastructure.config.RentalProperties;
import com.example.backend.presentation.dto.invoice.InvoiceRequest;
import com.example.backend.presentation.dto.invoice.InvoiceResponse;
import com.example.backend.presentation.exception.BusinessException;
import com.example.backend.presentation.exception.DuplicateResourceException;
import com.example.backend.presentation.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý hóa đơn tiền phòng/điện/nước: Lập hóa đơn hàng tháng, tự động tính tổng tiền và xác nhận thanh toán.
 */
@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final RentalProperties rentalProperties;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          ContractRepository contractRepository,
                          UserRepository userRepository,
                          RentalProperties rentalProperties) {
        this.invoiceRepository = invoiceRepository;
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
        this.rentalProperties = rentalProperties;
    }

    /**
     * Lấy danh sách tất cả hóa đơn trong hệ thống (dành cho Admin).
     */
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách hóa đơn theo hợp đồng cụ thể.
     */
    public List<InvoiceResponse> getInvoicesByContract(Long contractId) {
        return invoiceRepository.findByContractId(contractId).stream()
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách hóa đơn của khách hàng đang đăng nhập (/api/invoices/my).
     */
    public List<InvoiceResponse> getMyInvoices(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));
        return invoiceRepository.findByContractCustomerId(user.getId()).stream()
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết một hóa đơn theo ID.
     */
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));
        return InvoiceResponse.fromEntity(invoice);
    }

    /**
     * Lập hóa đơn tiền phòng/điện/nước cho một hợp đồng trong tháng/năm xác định.
     * Quy tắc nghiệp vụ:
     * - Kiểm tra xem hợp đồng đã xuất hóa đơn cho tháng/năm này chưa (tránh trùng lặp).
     * - Tự động tính tiền: Tiền phòng + (số ký điện * giá điện) + (số khối nước * giá nước).
     * - Trạng thái mặc định ban đầu là UNPAID.
     *
     * @param request dữ liệu số điện, số nước, tháng/năm và hợp đồng
     * @throws DuplicateResourceException nếu hóa đơn tháng này đã được lập trước đó
     */
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng với ID: " + request.getContractId()));

        // Kiểm tra tránh xuất hóa đơn 2 lần cho cùng một hợp đồng trong 1 tháng
        if (invoiceRepository.existsByContractIdAndMonthAndYear(request.getContractId(), request.getMonth(), request.getYear())) {
            throw new DuplicateResourceException("Hóa đơn cho hợp đồng này vào tháng " + request.getMonth() + "/" + request.getYear() + " đã tồn tại");
        }

        // Tính toán các khoản chi phí
        BigDecimal roomPrice = contract.getRoom() != null ? contract.getRoom().getPrice() : BigDecimal.ZERO;
        
        int electricityUsage = request.getElectricityNumber() != null ? request.getElectricityNumber() : 0;
        int waterUsage = request.getWaterNumber() != null ? request.getWaterNumber() : 0;

        // Tiền điện = số điện * đơn giá (cấu hình trong RentalProperties)
        BigDecimal electricityCost = rentalProperties.getElectricityPrice().multiply(BigDecimal.valueOf(electricityUsage));
        // Tiền nước = số nước * đơn giá
        BigDecimal waterCost = rentalProperties.getWaterPrice().multiply(BigDecimal.valueOf(waterUsage));

        // Tổng tiền = giá phòng + tiền điện + tiền nước
        BigDecimal totalAmount = roomPrice.add(electricityCost).add(waterCost);

        Invoice invoice = new Invoice();
        invoice.setContract(contract);
        invoice.setMonth(request.getMonth());
        invoice.setYear(request.getYear());
        invoice.setElectricityNumber(electricityUsage);
        invoice.setWaterNumber(waterUsage);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus(InvoiceStatus.UNPAID);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return InvoiceResponse.fromEntity(savedInvoice);
    }

    /**
     * Xác nhận thanh toán hóa đơn.
     * Chuyển trạng thái từ UNPAID sang PAID.
     *
     * @param id ID của hóa đơn cần thanh toán
     * @throws BusinessException nếu hóa đơn đã được thanh toán từ trước
     */
    @Transactional
    public InvoiceResponse payInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Hóa đơn này đã được thanh toán");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return InvoiceResponse.fromEntity(updatedInvoice);
    }
}
