package com.gm.hrms.controller;

import com.gm.hrms.dto.request.AttendanceCorrectionRequestDTO;
import com.gm.hrms.dto.request.AttendanceRequestDTO;
import com.gm.hrms.dto.response.AttendanceResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService service;

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> checkIn(
            @RequestBody AttendanceRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.<AttendanceResponseDTO>builder()
                        .success(true)
                        .message("Check-in successful")
                        .data(service.checkIn(dto))
                        .build()
        );
    }

    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> checkOut(
            @RequestBody AttendanceRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.<AttendanceResponseDTO>builder()
                        .success(true)
                        .message("Check-out successful")
                        .data(service.checkOut(dto))
                        .build()
        );
    }

    @PostMapping("/break-start")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> breakStart(
            @RequestBody AttendanceRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.<AttendanceResponseDTO>builder()
                        .success(true)
                        .message("Break started")
                        .data(service.breakStart(dto))
                        .build()
        );
    }

    @PostMapping("/break-end")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> breakEnd(
            @RequestBody AttendanceRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.<AttendanceResponseDTO>builder()
                        .success(true)
                        .message("Break ended")
                        .data(service.breakEnd(dto))
                        .build()
        );
    }

    @GetMapping("/today/{personalInformationId}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> todayAttendance(
            @PathVariable Long personalInformationId){

        return ResponseEntity.ok(
                ApiResponse.<AttendanceResponseDTO>builder()
                        .success(true)
                        .message("Today's attendance fetched")
                        .data(service.getTodayAttendance(personalInformationId))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<AttendanceResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDTO<AttendanceResponseDTO> response =
                service.getAllAttendance(PageRequest.of(page, size));

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<AttendanceResponseDTO>>builder()
                        .success(true)
                        .message("Attendance list fetched")
                        .data(response)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/correct")
    public ResponseEntity<?> correctAttendance(
            @RequestBody AttendanceCorrectionRequestDTO dto){

        return ResponseEntity.ok(
                ApiResponse.<AttendanceResponseDTO>builder()
                        .success(true)
                        .message("Attendance updated successful")
                        .data( service.correctAttendance(dto))
                        .build()
        );
    }
}