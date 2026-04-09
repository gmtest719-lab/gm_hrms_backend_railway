package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AttendanceStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAttendanceDetailDTO {

    private Long attendanceId;
    private LocalDate attendanceDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private AttendanceStatus status;
    private Integer workMinutes;
    private Integer breakMinutes;
    private Integer lateMinutes;
    private Integer overtimeMinutes;
    private boolean regularizationPending;
}