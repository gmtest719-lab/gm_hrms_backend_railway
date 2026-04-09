package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveHistoryReportDTO {
    private Long          id;
    private String        employeeCode;
    private String        employeeName;
    private String        department;
    private String        designation;
    private String        leaveType;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private Double        totalDays;
    private String        startDayType;
    private String        endDayType;
    private String        status;
    private String        reason;
    private LocalDateTime appliedOn;
}