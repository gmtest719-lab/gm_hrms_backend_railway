package com.gm.hrms.controller;

import com.gm.hrms.dto.request.UserCreateRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestBody @Valid UserCreateRequestDTO dto) {

        Object response = userService.create(dto);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("User created successfully")
                        .data(response)
                        .build()
        );
    }
}