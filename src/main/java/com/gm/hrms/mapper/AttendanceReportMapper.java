package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.service.UserCodeResolverService;

import java.util.Optional;

public class AttendanceReportMapper {

    private AttendanceReportMapper() {}

    public static DailyAttendanceReportDTO toDailyDTO(
            Attendance a,
            String employeeCode,
            UserCodeResolverService codeResolver) {

        PersonalInformation p  = a.getPersonalInformation();
        WorkProfile          wp = a.getWorkProfile();
        AttendanceCalculation c = a.getCalculation();

        return DailyAttendanceReportDTO.builder()
                .personalInformationId(p.getId())
                .employeeCode(employeeCode != null ? employeeCode
                        : codeResolver.getCode(p.getId()))
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .shift(wp != null && wp.getShift() != null
                        ? wp.getShift().getShiftName() : null)
                .attendanceDate(a.getAttendanceDate())
                .checkIn(a.getCheckIn())
                .checkOut(a.getCheckOut())
                .status(a.getStatus())
                .workMinutes(c != null ? c.getWorkMinutes() : null)
                .breakMinutes(c != null ? c.getBreakMinutes() : null)
                .lateMinutes(c != null ? c.getLateMinutes() : null)
                .overtimeMinutes(c != null ? c.getOvertimeMinutes() : null)
                .build();
    }

    public static LateComingReportDTO toLateDTO(Attendance a, String code) {
        PersonalInformation p  = a.getPersonalInformation();
        WorkProfile          wp = a.getWorkProfile();
        AttendanceCalculation c = a.getCalculation();
        String shiftStart = null;
        if (wp != null && wp.getShift() != null && wp.getShift().getTiming() != null) {
            shiftStart = wp.getShift().getTiming().getStartTime().toString();
        }
        return LateComingReportDTO.builder()
                .personalInformationId(p.getId())
                .employeeCode(code)
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .attendanceDate(a.getAttendanceDate())
                .checkIn(a.getCheckIn())
                .shiftStartTime(shiftStart)
                .lateMinutes(c != null ? c.getLateMinutes() : 0)
                .build();
    }

    public static OvertimeReportDTO toOvertimeDTO(Attendance a, String code) {
        PersonalInformation p  = a.getPersonalInformation();
        WorkProfile          wp = a.getWorkProfile();
        AttendanceCalculation c = a.getCalculation();
        return OvertimeReportDTO.builder()
                .personalInformationId(p.getId())
                .employeeCode(code)
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .attendanceDate(a.getAttendanceDate())
                .workMinutes(c != null ? c.getWorkMinutes() : 0)
                .overtimeMinutes(c != null ? c.getOvertimeMinutes() : 0)
                .build();
    }

    public static EmployeeAttendanceDetailDTO toDetailDTO(
            Attendance a,
            boolean hasPendingRegularization) {

        AttendanceCalculation c = a.getCalculation();
        return EmployeeAttendanceDetailDTO.builder()
                .attendanceId(a.getId())
                .attendanceDate(a.getAttendanceDate())
                .checkIn(a.getCheckIn())
                .checkOut(a.getCheckOut())
                .status(Optional.ofNullable(a.getStatus())
                        .orElse(AttendanceStatus.ABSENT))
                .workMinutes(c != null ? c.getWorkMinutes() : null)
                .breakMinutes(c != null ? c.getBreakMinutes() : null)
                .lateMinutes(c != null ? c.getLateMinutes() : null)
                .overtimeMinutes(c != null ? c.getOvertimeMinutes() : null)
                .regularizationPending(hasPendingRegularization)
                .build();
    }

    public static RegularizationReportDTO toRegularizationDTO(AttendanceRegularization r) {
        PersonalInformation p  = r.getRequestedBy();
        PersonalInformation rv = r.getReviewedBy();
        return RegularizationReportDTO.builder()
                .regularizationId(r.getId())
                .personalInformationId(p.getId())
                .employeeName(fullName(p))
                .attendanceDate(r.getAttendance().getAttendanceDate())
                .originalCheckIn(r.getOriginalCheckIn())
                .originalCheckOut(r.getOriginalCheckOut())
                .requestedCheckIn(r.getRequestedCheckIn())
                .requestedCheckOut(r.getRequestedCheckOut())
                .reason(r.getReason())
                .status(r.getStatus())
                .reviewerName(rv != null ? fullName(rv) : null)
                .reviewedAt(r.getReviewedAt())
                .remarks(r.getRemarks())
                .build();
    }

    private static String fullName(PersonalInformation p) {
        if (p == null) return "";
        String mid = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? p.getMiddleName() + " " : "";
        return p.getFirstName() + " " + mid + p.getLastName();
    }
}