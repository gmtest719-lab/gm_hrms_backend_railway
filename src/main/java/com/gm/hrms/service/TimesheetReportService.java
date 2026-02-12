package com.gm.hrms.service;

import com.gm.hrms.dto.response.TimesheetMonthlyDTO;
import com.gm.hrms.dto.response.TimesheetReportDTO;

import java.time.LocalDate;
import java.util.List;

public interface TimesheetReportService {

    List<TimesheetReportDTO> byDateRange(LocalDate start, LocalDate end);

    List<TimesheetReportDTO> byEmployee(
            Long empId,
            LocalDate start,
            LocalDate end
    );

    List<TimesheetReportDTO> byProject(
            Long projectId,
            LocalDate start,
            LocalDate end
    );

    List<TimesheetReportDTO> byStatus(
            String status,
            LocalDate start,
            LocalDate end
    );

    List<TimesheetReportDTO> todayAll();

    List<TimesheetReportDTO> todayByEmployee(Long empId);

    List<TimesheetReportDTO> todayByProject(Long projectId);

    List<TimesheetMonthlyDTO> monthly(int month, int year);

    TimesheetMonthlyDTO monthlyEmployee(Long empId, int month, int year);
}
