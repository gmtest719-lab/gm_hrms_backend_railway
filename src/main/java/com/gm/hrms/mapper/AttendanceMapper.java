package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.AttendanceResponseDTO;
import com.gm.hrms.entity.Attendance;

public class AttendanceMapper {

    private AttendanceMapper() {}

    public static AttendanceResponseDTO toResponse(Attendance a){

        if(a == null) return null;

        return AttendanceResponseDTO.builder()
                .id(a.getId())
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
