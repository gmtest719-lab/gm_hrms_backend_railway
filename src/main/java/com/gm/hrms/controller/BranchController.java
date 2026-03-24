package com.gm.hrms.controller;

import com.gm.hrms.dto.request.BranchRequestDTO;
import com.gm.hrms.dto.request.BranchUpdateDTO;
import com.gm.hrms.dto.response.BranchResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<BranchResponseDTO>> create(
            @Valid @RequestBody BranchRequestDTO dto) {

        BranchResponseDTO response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<BranchResponseDTO>builder()
                        .success(true)
                        .message("Branch created successfully")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody BranchUpdateDTO dto) {

        BranchResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<BranchResponseDTO>builder()
                        .success(true)
                        .message("Branch updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<BranchResponseDTO>builder()
                        .success(true)
                        .message("Branch fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<BranchResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<BranchResponseDTO>>builder()
                        .success(true)
                        .message("Branches fetched successfully")
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
                        .message("Branch deactivated successfully")
                        .build()
        );
    }
}