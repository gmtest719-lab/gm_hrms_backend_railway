package com.gm.hrms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTrendsReportDTO {
    private Integer month;
    private String  monthName;      // "January", "February" …
    private Integer year;
    private long    totalRequests;
    private Double  totalDaysTaken;
    private long    approvedCount;
    private long    pendingCount;
    private long    rejectedCount;
}
