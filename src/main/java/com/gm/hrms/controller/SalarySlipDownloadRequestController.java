package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.dto.request.SalarySlipDownloadApprovalDTO;
import com.gm.hrms.dto.request.SalarySlipDownloadRequestDTO;
import com.gm.hrms.dto.request.SalarySlipDownloadVerifyDTO;
import com.gm.hrms.dto.response.SalarySlipDownloadRequestResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.SalarySlipDownloadRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/download-requests")
@RequiredArgsConstructor
public class SalarySlipDownloadRequestController {

    private final SalarySlipDownloadRequestService service;

    // ═══════════════════════════════════════════════════════════════════════
    // EMPLOYEE ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping
    @Auditable(action = AuditAction.REQUEST_SALARY_SLIP_DOWNLOAD,
            resource = "SalarySlipDownloadRequest",
            description = "Employee requests salary slip download")
    public ResponseEntity<ApiResponse<SalarySlipDownloadRequestResponseDTO>> raiseRequest(
            @Valid @RequestBody SalarySlipDownloadRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<SalarySlipDownloadRequestResponseDTO>builder()
                        .success(true)
                        .message("Download request submitted successfully. "
                                + "You will receive an email once the admin approves it.")
                        .data(service.raiseRequest(dto))
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping("/status/{slipId}")
    public ResponseEntity<ApiResponse<SalarySlipDownloadRequestResponseDTO>> getStatus(
            @PathVariable Long slipId) {

        return ResponseEntity.ok(
                ApiResponse.<SalarySlipDownloadRequestResponseDTO>builder()
                        .success(true)
                        .data(service.getStatusBySlipId(slipId))
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping("/my/{personalId}")
    public ResponseEntity<ApiResponse<List<SalarySlipDownloadRequestResponseDTO>>> getMyRequests(
            @PathVariable Long personalId) {

        return ResponseEntity.ok(
                ApiResponse.<List<SalarySlipDownloadRequestResponseDTO>>builder()
                        .success(true)
                        .data(service.getMyRequests(personalId))
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping("/{slipId}/download")
    @Auditable(action = AuditAction.DOWNLOAD_SALARY_SLIP,
            resource = "SalarySlipDownloadRequest",
            description = "Employee verifies OTP and downloads salary slip PDF")
    public ResponseEntity<byte[]> verifyAndDownload(
            @PathVariable Long slipId,
            @Valid @RequestBody SalarySlipDownloadVerifyDTO dto) {

        byte[] pdf = service.verifyAndDownload(slipId, dto.getPassword());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=payslip-" + slipId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<SalarySlipDownloadRequestResponseDTO>>> getPending() {

        return ResponseEntity.ok(
                ApiResponse.<List<SalarySlipDownloadRequestResponseDTO>>builder()
                        .success(true)
                        .data(service.getPendingRequests())
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SalarySlipDownloadRequestResponseDTO>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.<List<SalarySlipDownloadRequestResponseDTO>>builder()
                        .success(true)
                        .data(service.getAllRequests())
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PutMapping("/{id}/resolve")
    @Auditable(action = AuditAction.RESOLVE_SALARY_SLIP_DOWNLOAD_REQUEST,
            resource = "SalarySlipDownloadRequest",
            description = "Admin approves or rejects salary slip download request")
    public ResponseEntity<ApiResponse<SalarySlipDownloadRequestResponseDTO>> resolve(
            @PathVariable Long id,
            @Valid @RequestBody SalarySlipDownloadApprovalDTO dto) {

        SalarySlipDownloadRequestResponseDTO response = service.resolveRequest(id, dto);

        String message = Boolean.TRUE.equals(dto.getApproved())
                ? "Request approved. An email with the download password has been sent to the employee."
                : "Request rejected. A notification email has been sent to the employee.";

        return ResponseEntity.ok(
                ApiResponse.<SalarySlipDownloadRequestResponseDTO>builder()
                        .success(true)
                        .message(message)
                        .data(response)
                        .build());
    }
}