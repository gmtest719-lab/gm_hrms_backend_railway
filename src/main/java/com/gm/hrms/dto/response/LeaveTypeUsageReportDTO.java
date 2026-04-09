package com.gm.hrms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeUsageReportDTO {
    private Long   leaveTypeId;
    private String leaveTypeName;
    private String leaveCode;
    private boolean isPaid;
    private long   totalRequests;
    private Double totalDaysTaken;
    private long   approvedCount;
    private long   pendingCount;
    private long   rejectedCount;
    private long   cancelledCount;
}
