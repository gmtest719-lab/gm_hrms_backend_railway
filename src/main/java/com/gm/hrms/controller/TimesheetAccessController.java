package com.gm.hrms.controller;

import com.gm.hrms.dto.request.TimesheetAccessRequestDTO;
import com.gm.hrms.dto.request.TimesheetAccessReviewDTO;
import com.gm.hrms.dto.response.TimesheetAccessResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.TimesheetAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timesheets/access-requests")
@RequiredArgsConstructor
public class TimesheetAccessController {

    private final TimesheetAccessService service;

    /** Employee requests access (edit old or extra work) */
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TimesheetAccessResponseDTO>> requestAccess(
            @RequestBody TimesheetAccessRequestDTO dto) {

        return ResponseEntity.ok(ApiResponse.<TimesheetAccessResponseDTO>builder()
                .success(true)
                .message("Access request submitted")
                .data(service.requestAccess(dto))
                .build());
    }

    /** Admin/HR approves or rejects */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}/review")
    public ResponseEntity<ApiResponse<TimesheetAccessResponseDTO>> review(
            @PathVariable Long id,
            @RequestBody TimesheetAccessReviewDTO dto) {

        return ResponseEntity.ok(ApiResponse.<TimesheetAccessResponseDTO>builder()
                .success(true)
                .message("Request reviewed")
                .data(service.reviewRequest(id, dto))
                .build());
    }

    /** List all pending requests (HR/Admin dashboard) */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<TimesheetAccessResponseDTO>>> getPending() {

        return ResponseEntity.ok(ApiResponse.<List<TimesheetAccessResponseDTO>>builder()
                .success(true)
                .message("Pending access requests")
                .data(service.getPendingRequests())
                .build());
    }

    /** Employee checks their own request history */
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','TRAINEE','INTERN')")
    @GetMapping("/person/{personId}")
    public ResponseEntity<ApiResponse<List<TimesheetAccessResponseDTO>>> getByPerson(
            @PathVariable Long personId) {

        return ResponseEntity.ok(ApiResponse.<List<TimesheetAccessResponseDTO>>builder()
                .success(true)
                .message("Access requests fetched")
                .data(service.getRequestsByPerson(personId))
                .build());
    }
}