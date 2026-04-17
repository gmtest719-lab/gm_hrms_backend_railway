package com.gm.hrms.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeReportResponseDTO<T> {

    private EmployeeReportSummaryDTO summary;
    private PageResponseDTO<T>       data;
}