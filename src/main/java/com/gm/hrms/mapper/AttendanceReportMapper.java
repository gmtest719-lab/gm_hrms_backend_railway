package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.AttendanceReportDTO;
import com.gm.hrms.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceReportMapper {

    public AttendanceReportDTO toDTO(Attendance a){

        return AttendanceReportDTO.builder()
                .employeeId(a.getEmployee().getId())
                .employeeName(
                        a.getEmployee().getFirstName() + " " +
                                a.getEmployee().getLastName()
                )
                .departmentName(
                        a.getEmployee().getDepartment().getName()
                )
                .date(a.getDate())
                .clockIn(a.getClockIn())
                .clockOut(a.getClockOut())
                .totalWorkingMinutes(a.getTotalWorkingMinutes())
                .totalBreakMinutes(a.getTotalBreakMinutes())
                .lateIn(a.getLateIn())
                .halfDay(a.getHalfDay())
                .build();
    }
}
