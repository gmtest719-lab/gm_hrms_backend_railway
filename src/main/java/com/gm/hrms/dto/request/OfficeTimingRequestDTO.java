package com.gm.hrms.dto.request;

import lombok.Data;

import java.time.LocalTime;

@Data
public class OfficeTimingRequestDTO {

    private LocalTime startTime;

    private Integer lateThresholdMinutes;
}

