package com.gm.hrms.service;

import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.dto.response.TimesheetResponseDTO;

import java.util.List;

public interface TimesheetService {

    TimesheetResponseDTO createOrUpdateTimesheet(TimesheetRequestDTO request);

    TimesheetResponseDTO submitTimesheet(Long timesheetId);

    TimesheetResponseDTO approveTimesheet(Long timesheetId);

    TimesheetResponseDTO rejectTimesheet(Long timesheetId);

    TimesheetResponseDTO getTimesheetById(Long id);

    TimesheetResponseDTO getByPersonAndDate(Long personId, String date);

    List<TimesheetResponseDTO> getAllTimesheets();

    void deleteTimesheet(Long id);

}