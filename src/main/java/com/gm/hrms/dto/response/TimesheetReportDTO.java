package com.gm.hrms.dto.response;

import com.gm.hrms.mapper.TimesheetStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TimesheetReportDTO {

    private Long timesheetId;

    private Long employeeId;
    private String employeeName;

    private Long projectId;
    private String projectName;

    private LocalDate workDate;

    private Double hours;
    private String description;
    private TimesheetStatus status;

}
