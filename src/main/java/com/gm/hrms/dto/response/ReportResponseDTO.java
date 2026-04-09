package com.gm.hrms.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDTO<T> {

    private AttendanceReportSummaryDTO summary;
    private PageResponseDTO<T> data;
}