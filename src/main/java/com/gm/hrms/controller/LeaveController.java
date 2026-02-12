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

    //  APPLY LEAVE
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

    //  APPROVE LEAVE
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

    //  REJECT LEAVE
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

    //  CANCEL LEAVE (MISSING IN YOUR CODE)
    @PatchMapping("/cancel/{leaveId}")
    public ResponseEntity<ApiResponse<?>> cancel(@PathVariable Long leaveId){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Leave cancelled")
                        .data(leaveService.cancel(leaveId))
                        .build()
        );
    }

    //  GET LEAVES BY EMPLOYEE (MISSING)
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<?>> getByEmployee(@PathVariable Long employeeId){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Employee Leaves")
                        .data(leaveService.getByEmployee(employeeId))
                        .build()
        );
    }

    //  GET ALL LEAVES (MISSING)
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("All leaves")
                        .data(leaveService.getAll())
                        .build()
        );
    }
}

