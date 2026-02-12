package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.LeaveReportDTO;
import com.gm.hrms.entity.LeaveApplication;
import org.springframework.stereotype.Component;

@Component
public class LeaveReportMapper {

    public LeaveReportDTO toDTO(LeaveApplication leave){

        return LeaveReportDTO.builder()
                .leaveId(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .employeeName(
                        leave.getEmployee().getFirstName() + " " +
                                leave.getEmployee().getLastName()
                )
                .departmentName(
                        leave.getEmployee().getDepartment().getName()
                )
                .leaveType(leave.getLeaveType().getName())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus().name())
                .cancelled(leave.getCancelled())
                .build();
    }
}

