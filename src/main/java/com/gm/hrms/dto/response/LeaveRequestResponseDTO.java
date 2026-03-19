package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LeaveRequestResponseDTO {
    private Long id;
    private String leaveType;
    private Double totalDays;
    private String status;
}