package com.gm.hrms.dto.response;

import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResponseDTO {

    private Long id;

    private String shiftName;

    private String shiftType;

    private Integer graceMinutes;

    private Integer minimumWorkHours;

    private Integer lateMarkAfterMinutes;

    private Integer lateMarkLimit;

    private Boolean overtimeAllowed;

    private Integer overtimeAfterMinutes;

    private Boolean autoCheckout;

    private ShiftTimingResponseDTO normalTiming;

    private List<ShiftDayConfigResponseDTO> dayConfigs;

    private List<BreakPolicyResponseDTO> breaks;
}