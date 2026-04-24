package com.gm.hrms.dto.request;

import com.gm.hrms.enums.TimesheetAccessStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimesheetAccessReviewDTO {

    @NotNull
    private Long reviewedById;

    @NotNull
    private TimesheetAccessStatus status; // APPROVED or REJECTED

    private String remarks;
}