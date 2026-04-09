package com.gm.hrms.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAttendanceReportDTO {

    private Long shiftId;
    private String shiftName;
    private String shiftType;
    private long totalEmployees;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private List<DailyAttendanceReportDTO> records;
}