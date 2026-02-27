package com.gm.hrms.controller;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.BankLegalDetailsRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.BankLegalDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank-legal")
@RequiredArgsConstructor
public class BankLegalDetailsController {

    private final BankLegalDetailsService service;

    //  ADMIN / HR can update
    @PostMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<?>> saveOrUpdate(
            @PathVariable Long employeeId,
            @Valid @RequestBody BankLegalDetailsRequestDTO requestDTO) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Bank details saved successfully")
                        .data(service.saveOrUpdate(employeeId, requestDTO))
                        .build()
        );
    }

    //  Employee can only view own details
    @GetMapping("/my-details")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> getMyDetails(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Bank details fetched successfully")
                        .data(service.getMyDetails(user.getUserId()))
                        .build()
        );
    }
}