package com.gm.hrms.controller;

import com.gm.hrms.dto.request.InternUpdateDTO;
import com.gm.hrms.dto.response.InternResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.InternService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
public class InternController {

    private final InternService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<InternResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody InternUpdateDTO dto) {

        InternResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<InternResponseDTO>builder()
                        .success(true)
                        .message("Intern updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternResponseDTO>> getById(
            @PathVariable Long id) {

        InternResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<InternResponseDTO>builder()
                        .success(true)
                        .message("Intern fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Intern deactivated successfully")
                        .build()
        );
    }
}
