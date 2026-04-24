package com.gm.hrms.dto.request;

import com.gm.hrms.enums.AttendanceStepStatus;
import com.gm.hrms.enums.AttendanceStepType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceProgressStepDTO {
    private int            orderIndex;
    private AttendanceStepType step;
    private AttendanceStepStatus status;
    private LocalDateTime timestamp;   // null when PENDING / IN_PROGRESS
}