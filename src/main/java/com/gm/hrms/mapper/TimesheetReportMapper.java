package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.TimesheetReportDTO;
import com.gm.hrms.entity.Timesheet;
import org.springframework.stereotype.Component;

@Component
public class TimesheetReportMapper {

    public TimesheetReportDTO toDTO(Timesheet t){

        return TimesheetReportDTO.builder()
                .timesheetId(t.getId())
                .employeeId(t.getEmployee().getId())
                .employeeName(
                        t.getEmployee().getFirstName() + " " +
                                t.getEmployee().getLastName()
                )
                .projectId(t.getProject().getId())
                .projectName(t.getProject().getProjectName())
                .workDate(t.getWorkDate())
                .hours(t.getHours())
                .description(t.getDescription())
                .status(t.getStatus())
                .build();
    }
}
