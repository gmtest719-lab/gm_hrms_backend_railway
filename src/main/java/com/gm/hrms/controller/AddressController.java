package com.gm.hrms.controller;

import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService service;

    // ================= CREATE =================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponseDTO>> create(
            @Valid @RequestBody AddressRequestDTO dto) {

        AddressResponseDTO response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<AddressResponseDTO>builder()
                        .success(true)
                        .message("Address created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE =================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDTO dto) {

        AddressResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<AddressResponseDTO>builder()
                        .success(true)
                        .message("Address updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> getById(
            @PathVariable Long id) {

        AddressResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<AddressResponseDTO>builder()
                        .success(true)
                        .message("Address fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> getAll() {

        List<AddressResponseDTO> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<AddressResponseDTO>>builder()
                        .success(true)
                        .message("Addresses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= DELETE =================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Address deleted successfully")
                        .build()
        );
    }
}