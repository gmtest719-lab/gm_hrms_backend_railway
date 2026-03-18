package com.gm.hrms.service;

import com.gm.hrms.dto.request.AttendanceCorrectionRequestDTO;
import com.gm.hrms.dto.request.AttendanceRequestDTO;
import com.gm.hrms.dto.response.AttendanceResponseDTO;

import java.util.List;

public interface AttendanceService {

    AttendanceResponseDTO checkIn(AttendanceRequestDTO dto);

    AttendanceResponseDTO checkOut(AttendanceRequestDTO dto);

    AttendanceResponseDTO breakStart(AttendanceRequestDTO dto);

    AttendanceResponseDTO breakEnd(AttendanceRequestDTO dto);

    AttendanceResponseDTO getTodayAttendance(Long personalInformationId);

    List<AttendanceResponseDTO> getAllAttendance();

    AttendanceResponseDTO correctAttendance(AttendanceCorrectionRequestDTO dto);

}