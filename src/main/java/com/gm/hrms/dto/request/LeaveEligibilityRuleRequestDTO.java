package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveEligibilityRuleRequestDTO {

    @NotNull
    private Long policyId;

    private Integer probationPeriodInMonths;

    private Boolean restrictPaidLeaveDuringProbation;

    private Boolean allowSickLeaveDuringProbation;

    private Boolean allowUnpaidLeaveDuringProbation;

    private Boolean allowCompOff;
}