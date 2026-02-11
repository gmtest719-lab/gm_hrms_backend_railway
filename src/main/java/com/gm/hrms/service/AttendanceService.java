package com.gm.hrms.service;

import com.gm.hrms.dto.response.AttendanceResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponseDTO clockIn(Long employeeId);

    AttendanceResponseDTO breakIn(Long employeeId);

    AttendanceResponseDTO breakOut(Long employeeId);

    AttendanceResponseDTO clockOut(Long employeeId);

    AttendanceResponseDTO getToday(Long employeeId);

    List<AttendanceResponseDTO> getAll();

    List<AttendanceResponseDTO> getByEmployee(Long employeeId);

    List<AttendanceResponseDTO> getByDateRange(LocalDate start, LocalDate end);

}

