package com.gm.hrms.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceMonthlyDTO {

    private Long employeeId;
    private String employeeName;

    private Integer totalDays;
    private Integer presentDays;
    private Integer lateDays;
    private Integer halfDays;

    private Integer totalWorkingMinutes;
}
