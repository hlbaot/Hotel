package com.example.backend.presentation.controller;

import com.example.backend.core.service.InvoiceService;
import com.example.backend.presentation.dto.invoice.InvoiceRequest;
import com.example.backend.presentation.dto.invoice.InvoiceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý hóa đơn tiền phòng/điện/nước:
 * - Admin: Tạo hóa đơn, xác nhận thanh toán, xem tất cả hóa đơn (/api/invoices/**)
 * - Khách: Xem hóa đơn cá nhân (/api/invoices/my)
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Lấy toàn bộ danh sách hóa đơn (chỉ ADMIN).
     * GET /api/invoices
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    /**
     * Khách hàng xem danh sách hóa đơn của chính mình.
     * GET /api/invoices/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<InvoiceResponse>> getMyInvoices(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(invoiceService.getMyInvoices(username));
    }

    /**
     * Xem hóa đơn theo ID hợp đồng.
     * GET /api/invoices/contract/{contractId}
     */
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByContract(@PathVariable Long contractId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByContract(contractId));
    }

    /**
     * Xem chi tiết hóa đơn theo ID.
     * GET /api/invoices/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    /**
     * Lập hóa đơn hàng tháng cho phòng (chỉ ADMIN).
     * POST /api/invoices
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse created = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Xác nhận thanh toán hóa đơn (chuyển sang PAID - chỉ ADMIN).
     * PUT /api/invoices/{id}/pay
     */
    @PutMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> payInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.payInvoice(id));
    }
}
