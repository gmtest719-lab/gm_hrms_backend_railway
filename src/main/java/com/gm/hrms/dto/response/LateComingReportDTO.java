package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LateComingReportDTO {

    private Long personalInformationId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private LocalDate attendanceDate;
    private LocalDateTime checkIn;
    private String shiftStartTime;
    private int lateMinutes;
}