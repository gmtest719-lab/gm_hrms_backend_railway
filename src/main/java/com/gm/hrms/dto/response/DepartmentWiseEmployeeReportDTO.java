package com.gm.hrms.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentWiseEmployeeReportDTO {

    private Long   departmentId;
    private String departmentName;
    private String departmentCode;

    private long totalEmployees;
    private long activeCount;
    private long inactiveCount;

    private List<EmployeeDirectoryDTO> employees;
}