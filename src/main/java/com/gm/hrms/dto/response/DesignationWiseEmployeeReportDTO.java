package com.gm.hrms.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignationWiseEmployeeReportDTO {

    private Long   designationId;
    private String designationName;

    private long totalEmployees;
    private long activeCount;
    private long inactiveCount;

    private List<EmployeeDirectoryDTO> employees;
}