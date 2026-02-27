package com.gm.hrms.dto.response;

import com.gm.hrms.enums.RoleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeSummaryDTO {

    private Long personalInformationId;
    private Long employeeId;
    private String employeeCode;
    private String fullName;
    private String departmentName;
    private RoleType role;
    private Boolean active;
}