package com.gm.hrms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveReportResponseDTO<T> {
    private LeaveReportSummaryDTO summary;
    private PageResponseDTO<T>    data;
}