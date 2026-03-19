package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.entity.LeaveRequest;
import com.gm.hrms.entity.LeaveType;
import com.gm.hrms.enums.LeaveStatus;

public class LeaveRequestMapper {

    public static LeaveRequest toEntity(LeaveRequestDTO dto, LeaveType leaveType) {
        return LeaveRequest.builder()
                .personalId(dto.getPersonalId())
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startDayType(dto.getStartDayType())
                .endDayType(dto.getEndDayType())
                .reason(dto.getReason())
                .status(LeaveStatus.PENDING)
                .approvalLevel(1)
                .isCancelled(false)
                .build();
    }

    public static LeaveRequestResponseDTO toResponse(LeaveRequest e) {
        return LeaveRequestResponseDTO.builder()
                .id(e.getId())
                .leaveType(e.getLeaveType().getName())
                .totalDays(e.getTotalDays())
                .status(e.getStatus().name())
                .build();
    }
}
