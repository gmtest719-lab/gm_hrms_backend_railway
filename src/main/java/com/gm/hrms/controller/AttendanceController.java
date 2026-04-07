package com.gm.hrms.controller;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;

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

    // ================= CHECK IN =================
    @PostMapping("/check-in")
    @Auditable(
            action      = AuditAction.ATTENDANCE_CHECK_IN,
            resource    = "Attendance",
            description = "Employee check-in"
    )
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

    // ================= CHECK OUT =================
    @PostMapping("/check-out")
    @Auditable(
            action      = AuditAction.ATTENDANCE_CHECK_OUT,
            resource    = "Attendance",
            description = "Employee check-out"
    )
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

    // ================= BREAK START =================
    @PostMapping("/break-start")
    @Auditable(
            action      = AuditAction.ATTENDANCE_BREAK_START,
            resource    = "Attendance",
            description = "Employee break started"
    )
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

    // ================= BREAK END =================
    @PostMapping("/break-end")
    @Auditable(
            action      = AuditAction.ATTENDANCE_BREAK_END,
            resource    = "Attendance",
            description = "Employee break ended"
    )
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

    // ================= TODAY =================
    @GetMapping("/today/{personalInformationId}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> todayAttendance(
            @PathVariable Long personalInformationId) {

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

    // ================= CORRECT =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/correct")
    @Auditable(
            action      = AuditAction.CORRECT_ATTENDANCE,
            resource    = "Attendance",
            description = "Admin/HR manual attendance correction"
    )
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