package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveEncashmentReportDTO {
    private String        employeeCode;
    private String        employeeName;
    private String        department;
    private String        designation;
    private String        leaveType;
    private Double        daysEncashed;
    private Double        beforeBalance;
    private Double        afterBalance;
    private LocalDateTime encashedOn;
    private String        remarks;
}
