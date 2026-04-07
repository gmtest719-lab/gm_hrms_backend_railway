package com.gm.hrms.dto.response;

import com.gm.hrms.enums.BreakCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class BreakPolicyResponseDTO {

    private Long id;

    private String breakName;

    private BreakCategory breakCategory;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    private Integer breakDurationMinutes;

    private Boolean isPaid;
}