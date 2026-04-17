package com.gm.hrms.dto.request;

import com.gm.hrms.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeReportFilterDTO {

    // ── Date range (joining / exit reports) ──────────────────
    private LocalDate fromDate;
    private LocalDate toDate;

    // ── Identity filters ──────────────────────────────────────
    private Long   personalInformationId;   // employee self-filter
    private Long   departmentId;
    private Long   designationId;
    private Long   branchId;

    // ── Status filters ────────────────────────────────────────
    private Boolean active;                 // true = Active, false = Inactive

    // ── Optional demographic filter ───────────────────────────
    private Gender gender;

}