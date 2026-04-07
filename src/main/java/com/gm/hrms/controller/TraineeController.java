package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.TraineeUpdateDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService service;

    // ================= UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Auditable(
            action      = AuditAction.UPDATE_TRAINEE,
            resource    = "Trainee",
            description = "Update trainee record"
    )
    public ResponseEntity<ApiResponse<TraineeResponseDTO>> update(
            @PathVariable Long id,
            @RequestParam("trainee") String traineeJson,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) Map<String, MultipartFile> documents,
            @RequestParam(required = false) Map<String, String> reasons
    ) throws Exception {

        return ResponseEntity.ok(
                ApiResponse.<TraineeResponseDTO>builder()
                        .success(true)
                        .message("Trainee updated successfully")
                        .data(service.update(id, traineeJson, profileImage, documents, reasons))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TraineeResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<TraineeResponseDTO>builder()
                        .success(true)
                        .message("Fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TraineeResponseDTO>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.<List<TraineeResponseDTO>>builder()
                        .success(true)
                        .message("Fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_TRAINEE,
            resource    = "Trainee",
            description = "Deactivate trainee"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Trainee deactivated successfully")
                        .build()
        );
    }
}