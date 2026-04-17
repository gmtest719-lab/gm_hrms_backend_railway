package com.gm.hrms.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeReportSummaryDTO {

    private long totalRecords;
    private long totalActive;
    private long totalInactive;
    private long totalDepartments;
    private long totalDesignations;

    // for joining report
    private long newJoinees;

    // for exit report
    private long totalExits;

    // for diversity
    private long maleCount;
    private long femaleCount;
}