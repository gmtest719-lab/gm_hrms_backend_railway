package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LeaveRequestResponseDTO {

    private Long id;

    // 🔹 Employee
    private String employeeName;
    private String employeeCode;

    // 🔹 Leave
    private String leaveType;

    private LocalDate startDate;
    private LocalDate endDate;
    private String dateRange;

    private double totalDays;

    private String reason;

    // 🔹 Status
    private String status;

    // 🔹 Metadata
    private LocalDateTime appliedOn;
}