package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    // ================= UPDATE =================
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Auditable(
            action      = AuditAction.UPDATE_EMPLOYEE,
            resource    = "Employee",
            description = "Update employee record"
    )
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> update(
            @PathVariable Long id,
            @RequestParam("employee") String employeeJson,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) Map<String, MultipartFile> documents,
            @RequestParam(required = false) Map<String, String> reasons
    ) throws Exception {

        EmployeeResponseDTO response =
                service.update(id, employeeJson, profileImage, documents, reasons);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponseDTO>builder()
                        .success(true)
                        .message("Employee updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    @Auditable(
            action      = AuditAction.VIEW_EMPLOYEE,
            resource    = "Employee",
            description = "View employee record"
    )
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getById(
            @PathVariable Long id) {

        EmployeeResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponseDTO>builder()
                        .success(true)
                        .message("Employee fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<EmployeeResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDTO<EmployeeResponseDTO> response =
                service.getAll(PageRequest.of(page, size));

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<EmployeeResponseDTO>>builder()
                        .success(true)
                        .message("Employees fetched successfully")
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
                        .message("Employee deactivated successfully")
                        .build()
        );
    }
}