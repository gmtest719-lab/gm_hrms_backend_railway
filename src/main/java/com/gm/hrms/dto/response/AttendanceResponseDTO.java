package com.gm.hrms.dto.response;

import com.gm.hrms.dto.request.AttendanceProgressStepDTO;
import com.gm.hrms.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class AttendanceResponseDTO {

    private Long   id;
    private Long   personalInformationId;

    // ── identity codes (only one will be non-null) ──
    private String employeeCode;
    private String traineeCode;
    private String internCode;

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private Integer workMinutes;
    private Integer breakMinutes;
    private Integer lateMinutes;
    private Integer overtimeMinutes;

    private AttendanceStatus status;

    // ── progress bar ──
    private List<AttendanceProgressStepDTO> progressSteps;
}