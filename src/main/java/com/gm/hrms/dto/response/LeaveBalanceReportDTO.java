package com.gm.hrms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceReportDTO {
    private Long   personalId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private String leaveType;
    private String leaveCode;
    private Double totalLeaves;
    private Double usedLeaves;
    private Double remainingLeaves;
    private Integer year;
}