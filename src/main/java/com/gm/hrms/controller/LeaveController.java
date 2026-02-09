package com.gm.hrms.controller;

import com.gm.hrms.dto.request.LeaveApplyRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/apply/{employeeId}")
    public ResponseEntity<ApiResponse<?>> apply(
            @PathVariable Long employeeId,
            @RequestBody LeaveApplyRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave applied successfully")
                        .data(leaveService.applyLeave(employeeId, dto))
                        .build()
        );
    }

    @PatchMapping("/approve/{leaveId}")
    public ResponseEntity<ApiResponse<?>> approve(@PathVariable Long leaveId){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave approved")
                        .data(leaveService.approve(leaveId))
                        .build()
        );
    }

    @PatchMapping("/reject/{leaveId}")
    public ResponseEntity<ApiResponse<?>> reject(@PathVariable Long leaveId){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave rejected")
                        .data(leaveService.reject(leaveId))
                        .build()
        );
    }
}
