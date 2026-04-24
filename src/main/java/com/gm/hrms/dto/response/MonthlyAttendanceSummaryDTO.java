package com.gm.hrms.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAttendanceSummaryDTO {

    private Long personalInformationId;
    private String employeeCode;
    private String traineeCode;
    private String internCode;
    private String employeeName;
    private String department;
    private String designation;
    private int month;
    private int year;
    private long totalPresent;
    private long totalAbsent;
    private long totalHalfDay;
    private long totalLeave;
    private long totalHoliday;
    private long totalWeekOff;
    private long totalLate;
    private long totalOvertime;
    private int totalWorkMinutes;
}