package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.BranchRequestDTO;
import com.gm.hrms.dto.request.BranchUpdateDTO;
import com.gm.hrms.dto.response.BranchMoveDTO;
import com.gm.hrms.dto.response.BranchResponseDTO;
import com.gm.hrms.dto.response.BranchTreeNodeDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_BRANCH,
            resource    = "Branch",
            description = "Create new branch"
    )
    public ResponseEntity<ApiResponse<BranchResponseDTO>> create(
            @Valid @RequestBody BranchRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<BranchResponseDTO>builder()
                        .success(true)
                        .message("Branch created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ================= UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_BRANCH,
            resource    = "Branch",
            description = "Update branch details"
    )
    public ResponseEntity<ApiResponse<BranchResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody BranchUpdateDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<BranchResponseDTO>builder()
                        .success(true)
                        .message("Branch updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<BranchResponseDTO>builder()
                        .success(true)
                        .message("Branch fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<BranchResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<BranchResponseDTO>>builder()
                        .success(true)
                        .message("Branches fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_BRANCH,
            resource    = "Branch",
            description = "Deactivate branch"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Branch deactivated successfully")
                        .build()
        );
    }

    // ================= GET TREE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<BranchTreeNodeDTO>>> getTree() {
        return ResponseEntity.ok(
                ApiResponse.<List<BranchTreeNodeDTO>>builder()
                        .success(true)
                        .message("Branch tree fetched successfully")
                        .data(service.getTree())
                        .build()
        );
    }

    // ================= MOVE (DRAG & DROP) =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}/move")
    @Auditable(
            action      = AuditAction.UPDATE_BRANCH,
            resource    = "Branch",
            description = "Move branch to new parent"
    )
    public ResponseEntity<ApiResponse<BranchResponseDTO>> move(
            @PathVariable Long id,
            @RequestBody BranchMoveDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<BranchResponseDTO>builder()
                        .success(true)
                        .message("Branch moved successfully")
                        .data(service.move(id, dto))
                        .build()
        );
    }
}