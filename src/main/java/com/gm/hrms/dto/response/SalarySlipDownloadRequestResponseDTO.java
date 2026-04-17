package com.gm.hrms.dto.response;

import com.gm.hrms.enums.DownloadRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SalarySlipDownloadRequestResponseDTO {

    private Long   id;
    private Long   slipId;
    private Long   personalId;
    private String employeeName;
    private String employeeCode;
    private String department;

    private Integer month;
    private Integer year;
    private String  monthYear;

    private DownloadRequestStatus status;

    /** True only when status = APPROVED — lets the UI enable the download button. */
    private boolean downloadEnabled;

    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
    private String        rejectionReason;
}