package com.gm.hrms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReportResponseDTO<T> {
    private ProjectReportSummaryDTO summary;
    private PageResponseDTO<T> data;
}