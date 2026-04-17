package com.gm.hrms.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeExitReportDTO {

    private Long      personalInformationId;
    private String    employeeCode;
    private String    fullName;
    private String    department;
    private String    designation;
    private String    branch;
    private LocalDate dateOfJoining;
    private LocalDate exitDate;
    private String    exitReason;        // RESIGNED / TERMINATED / CONTRACT_END etc.
    private String    exitRemarks;
    private String    role;
}