package com.gm.hrms.dto.request;

import com.gm.hrms.enums.BreakCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class BreakPolicyRequestDTO {

    private String breakName;

    private BreakCategory breakCategory;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    private Integer breakDurationMinutes;

    private Boolean isPaid;
}