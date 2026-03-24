package com.gm.hrms.controller;

import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.BreakPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/break-policies")
@RequiredArgsConstructor
public class BreakPolicyController {

    private final BreakPolicyService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> create(
            @Valid @RequestBody BreakPolicyRequestDTO dto) {

        BreakPolicyResponseDTO response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy created successfully")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody BreakPolicyRequestDTO dto) {

        BreakPolicyResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<BreakPolicyResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<BreakPolicyResponseDTO>>builder()
                        .success(true)
                        .message("Break policies fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Break policy deactivated successfully")
                        .build()
        );
    }
}