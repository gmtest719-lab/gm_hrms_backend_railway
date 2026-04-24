package com.gm.hrms.dto.response;

import com.gm.hrms.enums.RegularizationStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegularizationReportDTO {

    private Long regularizationId;
    private Long personalInformationId;
    private String employeeCode;
    private String traineeCode;
    private String internCode;
    private String employeeName;
    private LocalDate attendanceDate;
    private LocalDateTime originalCheckIn;
    private LocalDateTime originalCheckOut;
    private LocalDateTime requestedCheckIn;
    private LocalDateTime requestedCheckOut;
    private String reason;
    private RegularizationStatus status;
    private String reviewerName;
    private LocalDateTime reviewedAt;
    private String remarks;
}