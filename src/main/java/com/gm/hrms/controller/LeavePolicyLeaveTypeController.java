package com.gm.hrms.controller;

import com.gm.hrms.dto.request.LeavePolicyLeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyLeaveTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeavePolicyLeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policy-mapping")
@RequiredArgsConstructor
public class LeavePolicyLeaveTypeController {

    private final LeavePolicyLeaveTypeService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<LeavePolicyLeaveTypeResponseDTO>> create(
            @Valid @RequestBody LeavePolicyLeaveTypeRequestDTO request) {

        LeavePolicyLeaveTypeResponseDTO response = service.create(request);

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyLeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Mapping created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeavePolicyLeaveTypeResponseDTO>> getById(
            @PathVariable Long id) {

        LeavePolicyLeaveTypeResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyLeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Mapping fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeavePolicyLeaveTypeResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDTO<LeavePolicyLeaveTypeResponseDTO> response =
                service.getAll(PageRequest.of(page, size));

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeavePolicyLeaveTypeResponseDTO>>builder()
                        .success(true)
                        .message("Mappings fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= PATCH =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LeavePolicyLeaveTypeResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody LeavePolicyLeaveTypeRequestDTO request) {

        LeavePolicyLeaveTypeResponseDTO response = service.patchUpdate(id, request);

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyLeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Mapping updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Mapping deleted successfully")
                        .build()
        );
    }
}