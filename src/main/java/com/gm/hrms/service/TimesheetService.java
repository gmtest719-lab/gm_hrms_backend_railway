package com.gm.hrms.service;

import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.dto.response.TimesheetResponseDTO;

import java.util.List;

public interface TimesheetService {

    TimesheetResponseDTO create(Long employeeId, TimesheetRequestDTO dto);

    TimesheetResponseDTO submit(Long timesheetId);

    TimesheetResponseDTO approve(Long timesheetId);

    TimesheetResponseDTO reject(Long timesheetId);

    List<TimesheetResponseDTO> getByEmployee(Long employeeId);

    List<TimesheetResponseDTO> getAll();
}
