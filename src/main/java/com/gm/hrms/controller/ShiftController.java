package com.gm.hrms.controller;

import com.gm.hrms.dto.request.ShiftRequestDTO;
import com.gm.hrms.dto.response.ShiftResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> create(
            @Valid @RequestBody ShiftRequestDTO dto){

        ShiftResponseDTO response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<ShiftResponseDTO>builder()
                        .success(true)
                        .message("Shift created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getAll(){

        return ResponseEntity.ok(
                ApiResponse.<List<ShiftResponseDTO>>builder()
                        .success(true)
                        .data(service.getAll())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> getById(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.<ShiftResponseDTO>builder()
                        .success(true)
                        .data(service.getById(id))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id){

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Shift deleted successfully")
                        .build()
        );
    }
}
