package com.gm.hrms.dto.response;

import com.gm.hrms.enums.LeaveStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LeaveResponseDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private Boolean cancelled;

    private Long employeeId;
    private String employeeName;

    private String leaveTypeName;
}

