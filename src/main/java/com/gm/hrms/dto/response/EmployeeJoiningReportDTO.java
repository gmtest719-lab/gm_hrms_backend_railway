package com.gm.hrms.dto.response;

import com.gm.hrms.enums.EmploymentType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeJoiningReportDTO {

    private Long          personalInformationId;
    private String        employeeCode;
    private String        fullName;
    private String        department;
    private String        designation;
    private String        branch;
    private LocalDate     dateOfJoining;
    private EmploymentType employmentType;
    private String        role;
    private Boolean       active;
}