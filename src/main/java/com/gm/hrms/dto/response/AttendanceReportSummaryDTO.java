package com.gm.hrms.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceReportSummaryDTO {

    private long totalRecords;
    private long totalPresent;
    private long totalAbsent;
    private long totalHalfDay;
    private long totalLeave;
    private long totalHoliday;
    private long totalLate;
    private long totalOvertime;
}