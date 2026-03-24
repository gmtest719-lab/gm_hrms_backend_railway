package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttendanceResponseDTO {

    private Long id;

    private Long personalInformationId;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private Integer workMinutes;

    private Integer breakMinutes;

    private Integer lateMinutes;

    private Integer overtimeMinutes;

    private AttendanceStatus status;
}