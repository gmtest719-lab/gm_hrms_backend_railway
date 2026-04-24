package com.gm.hrms.dto.response;

import com.gm.hrms.enums.TimesheetAccessStatus;
import com.gm.hrms.enums.TimesheetAccessType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TimesheetAccessResponseDTO {

    private Long id;
    private Long personId;
    private String personName;
    private LocalDate requestedDate;
    private TimesheetAccessType accessType;
    private TimesheetAccessStatus status;
    private String reason;
    private String remarks;
    private LocalDateTime accessExpiresAt;
    private LocalDateTime reviewedAt;
}