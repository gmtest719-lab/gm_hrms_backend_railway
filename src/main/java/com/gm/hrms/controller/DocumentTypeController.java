package com.gm.hrms.controller;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.enums.ApplicableType;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.DocumentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {

    private final DocumentTypeService service;

    // ✅ CREATE → ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> create(
            @Valid @RequestBody DocumentTypeRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<DocumentTypeResponseDTO>builder()
                        .success(true)
                        .message("Document type created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ✅ UPDATE → ADMIN only
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody DocumentTypeRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<DocumentTypeResponseDTO>builder()
                        .success(true)
                        .message("Document type updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // ✅ DELETE → ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Document type deleted successfully")
                        .build()
        );
    }

    // ✅ GET ALL
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<DocumentTypeResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<DocumentTypeResponseDTO>>builder()
                        .success(true)
                        .message("Document types fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ✅ GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<DocumentTypeResponseDTO>builder()
                        .success(true)
                        .message("Document type fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ✅ FILTER BY TYPE
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<PageResponseDTO<DocumentTypeResponseDTO>>> getByApplicableType(
            @PathVariable ApplicableType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<DocumentTypeResponseDTO>>builder()
                        .success(true)
                        .message("Document types fetched successfully")
                        .data(service.getByApplicableType(type, PageRequest.of(page, size)))
                        .build()
        );
    }
}