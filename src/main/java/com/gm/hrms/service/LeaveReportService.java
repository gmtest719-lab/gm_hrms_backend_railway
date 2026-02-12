package com.gm.hrms.service;

import com.gm.hrms.dto.response.LeaveMonthlyDTO;
import com.gm.hrms.dto.response.LeaveReportDTO;
import com.gm.hrms.enums.LeaveStatus;

import java.time.LocalDate;
import java.util.List;

public interface LeaveReportService {

    List<LeaveReportDTO> byDateRange(LocalDate start, LocalDate end);

    List<LeaveReportDTO> byEmployee(Long empId, LocalDate start, LocalDate end);

    List<LeaveReportDTO> byDepartment(Long deptId, LocalDate start, LocalDate end);

    List<LeaveReportDTO> byStatus(
            LeaveStatus status,
            LocalDate start,
            LocalDate end
    );

    List<LeaveReportDTO> byLeaveType(
            Long leaveTypeId,
            LocalDate start,
            LocalDate end
    );

    List<LeaveReportDTO> todayAll();

    List<LeaveReportDTO> todayByEmployee(Long empId);

    List<LeaveReportDTO> todayByDepartment(Long deptId);

    List<LeaveMonthlyDTO> monthly(int month, int year);

    LeaveMonthlyDTO monthlyEmployee(Long empId, int month, int year);
}

