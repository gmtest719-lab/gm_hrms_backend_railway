package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalarySlipDownloadApprovalDTO {

    @NotNull(message = "Decision is required")
    private Boolean approved;           // true = APPROVED, false = REJECTED

    private String rejectionReason;
}