package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.LeaveResponseDTO;
import com.gm.hrms.entity.LeaveApplication;

public class LeaveMapper {

    public static LeaveResponseDTO toResponse(LeaveApplication leave){

        return LeaveResponseDTO.builder()
                .id(leave.getId())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .cancelled(leave.getCancelled())
                .employeeId(leave.getEmployee().getId())
                .employeeName(
                        leave.getEmployee().getFirstName() + " " +
                                leave.getEmployee().getLastName())
                .leaveTypeName(leave.getLeaveType().getName())
                .build();
    }
}

