package com.gm.hrms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveReportSummaryDTO {
    private long   totalRecords;
    private Double totalDaysTaken;
    private long   pendingCount;
    private long   approvedCount;
    private long   rejectedCount;
    private long   cancelledCount;
    private Double totalRemainingBalance;
}