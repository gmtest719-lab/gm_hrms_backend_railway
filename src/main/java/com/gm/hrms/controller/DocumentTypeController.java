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
@PreAuthorize("hasRole('ADMIN')")
public class DocumentTypeController {

    private final DocumentTypeService service;

    //  Create
    @PostMapping
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

    //  PATCH Update (Partial)
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody DocumentTypeRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<DocumentTypeResponseDTO>builder()
                        .success(true)
                        .message("Document type updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    //  Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Document type deleted successfully")
                        .build()
        );
    }

    //  Get All Active
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

    //  Get By ID
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

    //  Get By Applicable Type
    @GetMapping("/applicable/{type}")
    public ResponseEntity<ApiResponse<List<DocumentTypeResponseDTO>>> getByApplicableType(
            @PathVariable ApplicableType type) {

        return ResponseEntity.ok(
                ApiResponse.<List<DocumentTypeResponseDTO>>builder()
                        .success(true)
                        .message("Filtered document types fetched successfully")
                        .data(service.getByApplicableType(type))
                        .build()
        );
    }
}