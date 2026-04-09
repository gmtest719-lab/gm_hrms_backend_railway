// ===== AttendanceReportFilterDTO.java =====
package com.gm.hrms.dto.request;

import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.enums.RegularizationStatus;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceReportFilterDTO {

    // ── Date filters
    private LocalDate date;           // daily report exact date
    private LocalDate fromDate;       // range start
    private LocalDate toDate;         // range end
    private Integer   month;
    private Integer   year;

    // ── Entity filters
    private Long personalInformationId;
    private Long departmentId;
    private Long designationId;
    private Long shiftId;
    private Long branchId;

    // ── Status filters
    private AttendanceStatus      status;               // PRESENT / ABSENT / HALF_DAY / LATE / LEAVE ...
    private RegularizationStatus  regularizationStatus; // PENDING / APPROVED / REJECTED

    // ── Sort
    private String sortBy;    // "date" | "employeeName" | "status"
    private String sortDir;   // "asc" | "desc"
}