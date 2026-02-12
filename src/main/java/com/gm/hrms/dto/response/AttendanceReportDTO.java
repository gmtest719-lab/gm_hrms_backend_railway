package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceReportDTO {

    private Long employeeId;
    private String employeeName;
    private String departmentName;

    private LocalDate date;

    private LocalDateTime clockIn;
    private LocalDateTime clockOut;

    private Integer totalWorkingMinutes;
    private Integer totalBreakMinutes;

    private Boolean lateIn;
    private Boolean halfDay;
}

