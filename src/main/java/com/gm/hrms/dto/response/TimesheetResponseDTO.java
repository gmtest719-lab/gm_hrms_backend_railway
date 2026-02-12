package com.gm.hrms.dto.response;

import com.gm.hrms.mapper.TimesheetStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TimesheetResponseDTO {

    private Long id;
    private LocalDate workDate;
    private Double hours;
    private String description;
    private TimesheetStatus status;

    private String employeeName;
    private String projectName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

