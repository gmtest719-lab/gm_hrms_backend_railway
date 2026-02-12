package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimesheetMonthlyDTO {

    private Long employeeId;
    private String employeeName;

    private Double totalHours;
    private Integer totalEntries;
}
