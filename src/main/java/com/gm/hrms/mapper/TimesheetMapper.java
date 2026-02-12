package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.TimesheetResponseDTO;
import com.gm.hrms.entity.Timesheet;

public class TimesheetMapper {

    private TimesheetMapper(){}

    public static TimesheetResponseDTO toResponse(Timesheet t){

        return TimesheetResponseDTO.builder()
                .id(t.getId())
                .workDate(t.getWorkDate())
                .hours(t.getHours())
                .description(t.getDescription())
                .status(t.getStatus())
                .employeeName(
                        t.getEmployee().getFirstName() + " " +
                                t.getEmployee().getLastName()
                )
                .projectName(t.getProject().getProjectName())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}

