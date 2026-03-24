package com.gm.hrms.dto.request;

import com.gm.hrms.enums.ShiftType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShiftRequestDTO {

    private String shiftName;

    private ShiftType shiftType;

    private Integer graceMinutes;

    private Integer minimumWorkHours;

    private Integer lateMarkAfterMinutes;

    private Integer lateMarkLimit;

    private Boolean overtimeAllowed;

    private Integer overtimeAfterMinutes;

    private Boolean autoCheckout;

    private ShiftTimingDTO normalTiming;

    private List<ShiftDayConfigDTO> dayConfigs;

    private List<Long> breakIds;
}