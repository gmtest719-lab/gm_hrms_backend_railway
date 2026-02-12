package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveMonthlyDTO {

    private Long employeeId;
    private String employeeName;

    private Integer totalLeaves;
    private Integer approvedLeaves;
    private Integer rejectedLeaves;
    private Integer pendingLeaves;
    private Integer cancelledLeaves;
}

