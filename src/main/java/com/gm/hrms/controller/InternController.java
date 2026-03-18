package com.gm.hrms.controller;

import com.gm.hrms.dto.request.InternUpdateDTO;
import com.gm.hrms.dto.response.InternResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.InternService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
public class InternController {

    private final InternService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<InternResponseDTO>> update(

            @PathVariable Long id,

            @RequestParam("intern") String internJson,

            @RequestParam(required = false)
            MultipartFile profileImage,

            @RequestParam(required = false)
            Map<String, MultipartFile> documents,

            @RequestParam(required = false)
            Map<String, String> reasons

    ) throws Exception {

        InternResponseDTO response =
                service.update(id, internJson, profileImage, documents, reasons);

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
