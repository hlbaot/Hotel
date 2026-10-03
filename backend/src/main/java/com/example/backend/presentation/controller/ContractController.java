package com.example.backend.presentation.controller;

import com.example.backend.core.service.ContractService;
import com.example.backend.presentation.dto.contract.ContractRequest;
import com.example.backend.presentation.dto.contract.ContractResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý hợp đồng thuê phòng:
 * - Admin: Tạo, kết thúc và xem toàn bộ hợp đồng (/api/contracts/**)
 * - Khách: Xem các hợp đồng của chính mình (/api/contracts/my)
 */
@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * Lấy toàn bộ danh sách hợp đồng (chỉ ADMIN).
     * GET /api/contracts
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContractResponse>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    /**
     * Khách hàng xem danh sách hợp đồng của chính mình.
     * GET /api/contracts/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<ContractResponse>> getMyContracts(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(contractService.getMyContracts(username));
    }

    /**
     * Xem chi tiết hợp đồng theo ID.
     * GET /api/contracts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    /**
     * Tạo hợp đồng thuê phòng mới (chỉ ADMIN).
     * POST /api/contracts
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContractResponse> createContract(@Valid @RequestBody ContractRequest request) {
        ContractResponse created = contractService.createContract(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Kết thúc / thanh lý hợp đồng (chỉ ADMIN).
     * PUT /api/contracts/{id}/end
     */
    @PutMapping("/{id}/end")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContractResponse> endContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.endContract(id));
    }
}
