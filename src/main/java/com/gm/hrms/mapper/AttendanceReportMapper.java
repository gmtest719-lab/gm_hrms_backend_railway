package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.service.UserCodeResolverService;

import java.util.Optional;

public class AttendanceReportMapper {

    private AttendanceReportMapper() {}


    public static DailyAttendanceReportDTO toDailyDTO(
            Attendance a, UserCodeDTO codes, UserCodeResolverService resolver) {

        WorkProfile wp = a.getWorkProfile();
        AttendanceCalculation calc = a.getCalculation();

        return DailyAttendanceReportDTO.builder()
                .personalInformationId(a.getPersonalInformation().getId())
                .employeeCode(codes.getEmployeeCode())
                .traineeCode(codes.getTraineeCode())
                .internCode(codes.getInternCode())
                .employeeName(fullName(a.getPersonalInformation()))
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : null)
                .shift(wp != null && wp.getShift() != null ? wp.getShift().getShiftName() : null)
                .attendanceDate(a.getAttendanceDate())
                .checkIn(a.getCheckIn())
                .checkOut(a.getCheckOut())
                .status(a.getStatus())
                .workMinutes(calc != null ? calc.getWorkMinutes() : null)
                .lateMinutes(calc != null ? calc.getLateMinutes() : null)
                .overtimeMinutes(calc != null ? calc.getOvertimeMinutes() : null)
                .build();
    }

    public static LateComingReportDTO toLateDTO(Attendance a, UserCodeDTO codes) {
        WorkProfile wp = a.getWorkProfile();
        AttendanceCalculation calc = a.getCalculation();
        String shiftStart = resolveShiftStart(wp, a.getAttendanceDate());

        return LateComingReportDTO.builder()
                .personalInformationId(a.getPersonalInformation().getId())
                .employeeCode(codes.getEmployeeCode())
                .traineeCode(codes.getTraineeCode())
                .internCode(codes.getInternCode())
                .employeeName(fullName(a.getPersonalInformation()))
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : null)
                .attendanceDate(a.getAttendanceDate())
                .checkIn(a.getCheckIn())
                .shiftStartTime(shiftStart)
                .lateMinutes(calc != null ? calc.getLateMinutes() : null)
                .build();
    }

    public static OvertimeReportDTO toOvertimeDTO(Attendance a, UserCodeDTO codes) {
        WorkProfile wp = a.getWorkProfile();
        AttendanceCalculation calc = a.getCalculation();

        return OvertimeReportDTO.builder()
                .personalInformationId(a.getPersonalInformation().getId())
                .employeeCode(codes.getEmployeeCode())
                .traineeCode(codes.getTraineeCode())
                .internCode(codes.getInternCode())
                .employeeName(fullName(a.getPersonalInformation()))
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : null)
                .attendanceDate(a.getAttendanceDate())
                .workMinutes(calc != null ? calc.getWorkMinutes() : null)
                .overtimeMinutes(calc != null ? calc.getOvertimeMinutes() : null)
                .build();
    }

    public static RegularizationReportDTO toRegularizationDTO(AttendanceRegularization r) {
        PersonalInformation p = r.getRequestedBy();
        return RegularizationReportDTO.builder()
                .id(r.getId())
                .personalInformationId(p.getId())
                // codes are set by the caller after resolving
                .employeeName(fullName(p))
                .attendanceDate(r.getAttendance().getAttendanceDate())
                .originalCheckIn(r.getOriginalCheckIn())
                .originalCheckOut(r.getOriginalCheckOut())
                .requestedCheckIn(r.getRequestedCheckIn())
                .requestedCheckOut(r.getRequestedCheckOut())
                .reason(r.getReason())
                .status(r.getStatus())
                .remarks(r.getRemarks())
                .reviewedAt(r.getReviewedAt())
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


    private static String fullName(PersonalInformation p) {
        if (p == null) return "";
        String mid = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? p.getMiddleName() + " " : "";
        return p.getFirstName() + " " + mid + p.getLastName();
    }
}