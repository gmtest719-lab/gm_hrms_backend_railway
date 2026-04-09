package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApprovalReportDTO {
    private Long          requestId;
    private String        employeeCode;
    private String        employeeName;
    private String        department;
    private String        designation;
    private String        leaveType;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private Double        totalDays;
    private String        status;
    private String        approverCode;
    private String        approverName;
    private LocalDateTime approvedAt;
    private String        rejectionReason;
    private LocalDateTime appliedOn;
}