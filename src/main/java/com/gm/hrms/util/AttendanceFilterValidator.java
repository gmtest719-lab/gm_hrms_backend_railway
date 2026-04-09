package com.gm.hrms.util;

import com.gm.hrms.dto.request.AttendanceReportFilterDTO;
import com.gm.hrms.exception.InvalidRequestException;
import java.time.LocalDate;

public class AttendanceFilterValidator {

    private AttendanceFilterValidator() {}

    /**
     * Applies sensible defaults and validates ranges.
     * Call this at the TOP of every service method.
     */
    public static AttendanceReportFilterDTO validateAndDefault(
            AttendanceReportFilterDTO filter) {

        if (filter == null) filter = new AttendanceReportFilterDTO();

        // ── Default date to today if nothing supplied ──
        if (filter.getDate() == null
                && filter.getFromDate() == null
                && filter.getToDate() == null
                && filter.getMonth() == null) {
            filter.setDate(LocalDate.now());
        }

        // ── Ensure fromDate <= toDate ──
        if (filter.getFromDate() != null && filter.getToDate() != null
                && filter.getFromDate().isAfter(filter.getToDate())) {
            throw new InvalidRequestException("fromDate must not be after toDate");
        }

        // ── Range must not exceed 366 days (prevent accidental full-table scans) ──
        if (filter.getFromDate() != null && filter.getToDate() != null) {
            long days = filter.getToDate().toEpochDay() - filter.getFromDate().toEpochDay();
            if (days > 366) {
                throw new InvalidRequestException(
                        "Date range cannot exceed 366 days. Use monthly summary for longer periods.");
            }
        }

        // ── Default month/year to current if either is missing ──
        if (filter.getMonth() != null && filter.getYear() == null) {
            filter.setYear(LocalDate.now().getYear());
        }
        if (filter.getYear() != null && filter.getMonth() == null) {
            filter.setMonth(LocalDate.now().getMonthValue());
        }

        // ── Valid month ──
        if (filter.getMonth() != null
                && (filter.getMonth() < 1 || filter.getMonth() > 12)) {
            throw new InvalidRequestException("month must be between 1 and 12");
        }

        // ── Default sort ──
        if (filter.getSortBy()  == null) filter.setSortBy("attendanceDate");
        if (filter.getSortDir() == null) filter.setSortDir("asc");

        return filter;
    }
}