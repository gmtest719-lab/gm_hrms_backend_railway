package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveEligibilityRuleResponseDTO {

    private Long id;
    private Long policyId;

    private Integer probationPeriodInMonths;

    private Boolean restrictPaidLeaveDuringProbation;
    private Boolean allowSickLeaveDuringProbation;
    private Boolean allowUnpaidLeaveDuringProbation;
    private Boolean allowCompOff;

    private Boolean isActive;
}