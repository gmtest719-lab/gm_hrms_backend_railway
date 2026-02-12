package com.gm.hrms.service;

import com.gm.hrms.dto.response.AttendanceMonthlyDTO;
import com.gm.hrms.dto.response.AttendanceReportDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceReportService {

    List<AttendanceReportDTO> byDateRange(LocalDate start, LocalDate end);

    List<AttendanceReportDTO> byEmployee(
            Long empId,
            LocalDate start,
            LocalDate end
    );

    List<AttendanceReportDTO> byDepartment(
            Long deptId,
            LocalDate start,
            LocalDate end
    );

    List<AttendanceReportDTO> late(LocalDate start, LocalDate end);

    List<AttendanceReportDTO> halfDay(LocalDate start, LocalDate end);

    List<AttendanceReportDTO> todayAll();

    List<AttendanceReportDTO> todayByDepartment(Long deptId);

    List<AttendanceReportDTO> todayByEmployee(Long empId);

    List<AttendanceMonthlyDTO> monthly(int month, int year);

    AttendanceMonthlyDTO monthlyEmployee(
            Long empId,
            int month,
            int year
    );
}

