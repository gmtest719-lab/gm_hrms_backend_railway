package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.ChangePasswordRequestDTO;
import com.gm.hrms.dto.request.LoginRequestDTO;
import com.gm.hrms.dto.request.LogoutRequestDTO;
import com.gm.hrms.dto.request.RefreshTokenRequestDTO;
import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ⭐ LOGIN
    @PostMapping("/login")
    @Auditable(
            action      = AuditAction.LOGIN,
            resource    = "Auth",
            description = "User login attempt"
    )
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<LoginResponseDTO>builder()
                        .success(true)
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }

    // ⭐ REFRESH TOKEN
    @PostMapping("/refresh")
    @Auditable(
            action      = AuditAction.REFRESH_TOKEN,
            resource    = "Auth",
            description = "Access token refresh"
    )
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refresh(
            @RequestBody RefreshTokenRequestDTO request) {

        LoginResponseDTO response =
                authService.refreshToken(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<LoginResponseDTO>builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .data(response)
                        .build()
        );
    }

    // ⭐ LOGOUT
    @PostMapping("/logout")
    @Auditable(
            action      = AuditAction.LOGOUT,
            resource    = "Auth",
            description = "User logout"
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody LogoutRequestDTO request) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logout successful")
                        .build()
        );
    }

    // ⭐ CHANGE PASSWORD
    @PostMapping("/change-password")
    @Auditable(
            action      = AuditAction.CHANGE_PASSWORD,
            resource    = "Auth",
            description = "Password change request"
    )
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestBody ChangePasswordRequestDTO request) {

        authService.changePassword(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Password changed successfully")
                        .build()
        );
    }
}