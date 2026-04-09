package com.gm.hrms.dto.request;

import com.gm.hrms.enums.LeaveStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveReportFilterDTO {

    private LocalDate fromDate;
    private LocalDate toDate;

    private Long personalId;

    private Long departmentId;
    private Long designationId;

    private Long     leaveTypeId;
    private LeaveStatus status;
    private Long     approverId;

    private Integer month;
    private Integer year;
}