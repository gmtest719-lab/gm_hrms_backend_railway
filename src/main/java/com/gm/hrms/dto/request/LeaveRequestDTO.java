package com.gm.hrms.dto.request;

import com.gm.hrms.enums.DayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDTO {

    @NotNull
    private Long personalId;

    @NotNull
    private Long leaveTypeId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private DayType startDayType = DayType.FULL;
    private DayType endDayType = DayType.FULL;

    @NotBlank
    private String reason;
}