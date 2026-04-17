package com.gm.hrms.dto.response;

import com.gm.hrms.enums.EmploymentType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeStatusReportDTO {

    private Long          personalInformationId;
    private String        employeeCode;
    private String        fullName;
    private String        department;
    private String        designation;
    private EmploymentType employmentType;
    private Boolean       active;
    private String        recordStatus;
    private LocalDate     dateOfJoining;
    private String        role;
}