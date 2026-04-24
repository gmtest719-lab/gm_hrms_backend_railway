package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.AttendanceProgressStepDTO;
import com.gm.hrms.dto.response.AttendanceResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AttendanceLogType;
import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.enums.AttendanceStepStatus;
import com.gm.hrms.enums.AttendanceStepType;

import java.time.LocalDateTime;
import java.util.List;

public final class AttendanceMapper {

    private AttendanceMapper(){}

    public static Attendance createAttendance(
            PersonalInformation person,
            WorkProfile profile
    ){

        LocalDateTime now = LocalDateTime.now();

        return Attendance.builder()
                .personalInformation(person)
                .workProfile(profile)
                .attendanceDate(now.toLocalDate())
                .checkIn(now)
                .status(AttendanceStatus.PRESENT)
                .build();
    }

    public static AttendanceLog createCheckInLog(PersonalInformation person){

        return AttendanceLog.builder()
                .personalInformation(person)
                .logTime(LocalDateTime.now())
                .logType(AttendanceLogType.CHECK_IN)
                .deviceType("WEB")
                .build();
    }

    public static AttendanceLog createCheckOutLog(PersonalInformation person){

        return AttendanceLog.builder()
                .personalInformation(person)
                .logTime(LocalDateTime.now())
                .logType(AttendanceLogType.CHECK_OUT)
                .deviceType("WEB")
                .build();
    }

    public static AttendanceBreakLog createBreakStart(Attendance attendance){

        return AttendanceBreakLog.builder()
                .attendance(attendance)
                .breakStart(LocalDateTime.now())
                .build();
    }

    public static AttendanceCalculation createCalculation(
            Attendance attendance,
            int workMinutes,
            int breakMinutes,
            int lateMinutes,
            int overtimeMinutes
    ){

        return AttendanceCalculation.builder()
                .attendance(attendance)
                .workMinutes(workMinutes)
                .breakMinutes(breakMinutes)
                .lateMinutes(lateMinutes)
                .overtimeMinutes(overtimeMinutes)
                .build();
    }

    public static AttendanceResponseDTO toResponse(
            Attendance           attendance,
            AttendanceCalculation calc,
            String               employeeCode,   // null if not applicable
            String               traineeCode,
            String               internCode,
            AttendanceBreakLog   latestBreakLog  // null if none today
    ) {
        return AttendanceResponseDTO.builder()
                .id(attendance.getId())
                .personalInformationId(attendance.getPersonalInformation().getId())
                .employeeCode(employeeCode)
                .traineeCode(traineeCode)
                .internCode(internCode)
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .workMinutes(calc != null ? calc.getWorkMinutes() : null)
                .breakMinutes(calc != null ? calc.getBreakMinutes() : null)
                .lateMinutes(calc != null ? calc.getLateMinutes() : null)
                .overtimeMinutes(calc != null ? calc.getOvertimeMinutes() : null)
                .status(attendance.getStatus())
                .progressSteps(buildProgressSteps(attendance, latestBreakLog))
                .build();
    }

    private static List<AttendanceProgressStepDTO> buildProgressSteps(
            Attendance        attendance,
            AttendanceBreakLog latestBreak
    ) {
        boolean checkedIn   = attendance.getCheckIn()  != null;
        boolean checkedOut  = attendance.getCheckOut() != null;
        boolean breakOpen   = latestBreak != null && latestBreak.getBreakEnd() == null;
        boolean breakClosed = latestBreak != null && latestBreak.getBreakEnd() != null;

        return List.of(
                AttendanceProgressStepDTO.builder()
                        .orderIndex(1)
                        .step(AttendanceStepType.CHECK_IN)
                        .status(checkedIn
                                ? AttendanceStepStatus.COMPLETED
                                : AttendanceStepStatus.IN_PROGRESS)
                        .timestamp(attendance.getCheckIn())
                        .build(),

                AttendanceProgressStepDTO.builder()
                        .orderIndex(2)
                        .step(AttendanceStepType.BREAK_START)
                        .status(!checkedIn || checkedOut
                                ? AttendanceStepStatus.PENDING
                                : (breakOpen || breakClosed)
                                ? AttendanceStepStatus.COMPLETED
                                : AttendanceStepStatus.IN_PROGRESS)
                        .timestamp(latestBreak != null ? latestBreak.getBreakStart() : null)
                        .build(),

                AttendanceProgressStepDTO.builder()
                        .orderIndex(3)
                        .step(AttendanceStepType.BREAK_END)
                        .status(!checkedIn || checkedOut || (!breakOpen && !breakClosed)
                                ? AttendanceStepStatus.PENDING
                                : breakClosed
                                ? AttendanceStepStatus.COMPLETED
                                : AttendanceStepStatus.IN_PROGRESS)
                        .timestamp(latestBreak != null ? latestBreak.getBreakEnd() : null)
                        .build(),

                AttendanceProgressStepDTO.builder()
                        .orderIndex(4)
                        .step(AttendanceStepType.CHECK_OUT)
                        .status(checkedOut
                                ? AttendanceStepStatus.COMPLETED
                                : checkedIn
                                ? AttendanceStepStatus.IN_PROGRESS
                                : AttendanceStepStatus.PENDING)
                        .timestamp(attendance.getCheckOut())
                        .build()
        );
    }

}