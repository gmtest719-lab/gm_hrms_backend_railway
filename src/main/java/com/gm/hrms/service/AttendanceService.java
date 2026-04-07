package com.gm.hrms.service;

import com.gm.hrms.dto.request.AttendanceCorrectionRequestDTO;
import com.gm.hrms.dto.request.AttendanceRequestDTO;
import com.gm.hrms.dto.response.AttendanceResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttendanceService {

    AttendanceResponseDTO checkIn(AttendanceRequestDTO dto);

    AttendanceResponseDTO checkOut(AttendanceRequestDTO dto);

    AttendanceResponseDTO breakStart(AttendanceRequestDTO dto);

    AttendanceResponseDTO breakEnd(AttendanceRequestDTO dto);

    AttendanceResponseDTO getTodayAttendance(Long personalInformationId);

    PageResponseDTO<AttendanceResponseDTO> getAllAttendance(Pageable pageable);

    AttendanceResponseDTO correctAttendance(AttendanceCorrectionRequestDTO dto);

}