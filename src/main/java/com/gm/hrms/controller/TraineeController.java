package com.gm.hrms.controller;

import com.gm.hrms.dto.request.TraineeRequestDTO;
import com.gm.hrms.dto.request.TraineeUpdateDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService service;

    @PostMapping("/{personalId}")
    public ResponseEntity<ApiResponse<UserCreateResponseDTO>> create(
            @PathVariable Long personalId,
            @RequestBody TraineeRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<UserCreateResponseDTO>builder()
                        .success(true)
                        .message("Trainee created successfully")
                        .data(service.create(dto, personalId))
                        .build()
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TraineeResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody TraineeUpdateDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<TraineeResponseDTO>builder()
                        .success(true)
                        .message("Trainee updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

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

    @DeleteMapping("/{id}")
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