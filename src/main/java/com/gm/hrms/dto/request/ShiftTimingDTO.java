package com.gm.hrms.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ShiftTimingDTO {

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalTime checkinStartWindow;

    private LocalTime checkinEndWindow;

    private LocalTime checkoutStartWindow;

    private LocalTime checkoutEndWindow;

    private Boolean saturdayOff;

    private Boolean sundayOff;
}
