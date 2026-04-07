package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.InternshipDomainRequestDTO;
import com.gm.hrms.dto.response.InternshipDomainResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.InternshipDomainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internship-domains")
@RequiredArgsConstructor
public class InternshipDomainController {

    private final InternshipDomainService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_INTERNSHIP_DOMAIN,
            resource    = "InternshipDomain",
            description = "Create internship domain"
    )
    public ResponseEntity<ApiResponse<InternshipDomainResponseDTO>> create(
            @Valid @RequestBody InternshipDomainRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<InternshipDomainResponseDTO>builder()
                        .success(true)
                        .message("Internship domain created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ================= UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_INTERNSHIP_DOMAIN,
            resource    = "InternshipDomain",
            description = "Update internship domain"
    )
    public ResponseEntity<ApiResponse<InternshipDomainResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody InternshipDomainRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<InternshipDomainResponseDTO>builder()
                        .success(true)
                        .message("Internship domain updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternshipDomainResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<InternshipDomainResponseDTO>builder()
                        .success(true)
                        .message("Internship domain fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<InternshipDomainResponseDTO>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.<List<InternshipDomainResponseDTO>>builder()
                        .success(true)
                        .message("Internship domains fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_INTERNSHIP_DOMAIN,
            resource    = "InternshipDomain",
            description = "Deactivate internship domain"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Internship domain deactivated successfully")
                        .build()
        );
    }
}