package com.gm.hrms.controller;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.enums.ApplicableType;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.DocumentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {

    private final DocumentTypeService service;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentTypeResponseDTO>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.<List<DocumentTypeResponseDTO>>builder()
                        .success(true)
                        .message("Document types fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

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

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<DocumentTypeResponseDTO>>> getByApplicableType(
            @PathVariable ApplicableType type) {

        return ResponseEntity.ok(
                ApiResponse.<List<DocumentTypeResponseDTO>>builder()
                        .success(true)
                        .message("Document types fetched successfully")
                        .data(service.getByApplicableType(type))
                        .build()
        );
    }
}