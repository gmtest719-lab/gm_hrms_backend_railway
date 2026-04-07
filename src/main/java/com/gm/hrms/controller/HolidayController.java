package com.gm.hrms.controller;

import com.gm.hrms.dto.request.HolidayRequestDTO;
import com.gm.hrms.dto.response.HolidayResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> create(
            @Valid @RequestBody HolidayRequestDTO dto){

        HolidayResponseDTO response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponseDTO>builder()
                        .success(true)
                        .message("Holiday created successfully")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<HolidayResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<HolidayResponseDTO>>builder()
                        .success(true)
                        .message("Holidays fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> getById(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponseDTO>builder()
                        .success(true)
                        .data(service.getById(id))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody HolidayRequestDTO dto){

        HolidayResponseDTO response = service.update(id,dto);

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponseDTO>builder()
                        .success(true)
                        .message("Holiday updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id){

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Holiday deleted successfully")
                        .build()
        );
    }
}