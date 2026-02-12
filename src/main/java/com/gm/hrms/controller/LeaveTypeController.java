package com.gm.hrms.controller;

import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@RequestBody LeaveTypeRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave type created")
                        .data(service.create(dto))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .data(service.getAll())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id){

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave type deleted")
                        .build()
        );
    }
}

