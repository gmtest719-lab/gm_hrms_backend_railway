package com.gm.hrms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LeaveReportDTO {

    private Long leaveId;

    private Long employeeId;
    private String employeeName;
    private String departmentName;

    private String leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    private String reason;

    private String status;

    private Boolean cancelled;
}

