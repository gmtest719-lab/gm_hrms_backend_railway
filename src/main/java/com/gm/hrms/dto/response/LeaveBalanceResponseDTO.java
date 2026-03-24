package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveBalanceResponseDTO {

    private String employeeName;
    private String employeeCode;
    private String designation;
    private String department;

    private String leaveType;

    private double totalLeaves;
    private double usedLeaves;
    private double remainingLeaves;

    private Integer year;
}