package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class OfficeTimingResponseDTO {

    private Long id;
    private LocalTime startTime;
    private Integer lateThresholdMinutes;
}
