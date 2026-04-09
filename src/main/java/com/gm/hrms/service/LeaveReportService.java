package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import com.gm.hrms.dto.response.*;
import org.springframework.data.domain.Pageable;

public interface LeaveReportService {

    LeaveReportResponseDTO<LeaveBalanceReportDTO> getBalanceReport(
            LeaveReportFilterDTO filter, Pageable pageable);

    LeaveReportResponseDTO<LeaveHistoryReportDTO> getHistoryReport(
            LeaveReportFilterDTO filter, Pageable pageable);

    LeaveReportResponseDTO<LeaveRequestReportDTO> getRequestReport(
            LeaveReportFilterDTO filter, Pageable pageable);

    LeaveReportResponseDTO<LeaveTypeUsageReportDTO> getTypeUsageReport(
            LeaveReportFilterDTO filter, Pageable pageable);

    LeaveReportResponseDTO<LeaveTrendsReportDTO> getTrendsReport(
            LeaveReportFilterDTO filter, Pageable pageable);

    LeaveReportResponseDTO<LeaveEncashmentReportDTO> getEncashmentReport(
            LeaveReportFilterDTO filter, Pageable pageable);

    LeaveReportResponseDTO<LeaveApprovalReportDTO> getApprovalReport(
            LeaveReportFilterDTO filter, Pageable pageable);
}