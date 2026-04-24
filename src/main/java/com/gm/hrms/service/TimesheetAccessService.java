package com.gm.hrms.service;

import com.gm.hrms.dto.request.TimesheetAccessRequestDTO;
import com.gm.hrms.dto.request.TimesheetAccessReviewDTO;
import com.gm.hrms.dto.response.TimesheetAccessResponseDTO;

import java.util.List;

public interface TimesheetAccessService {

    TimesheetAccessResponseDTO requestAccess(TimesheetAccessRequestDTO dto);

    TimesheetAccessResponseDTO reviewRequest(Long requestId, TimesheetAccessReviewDTO dto);

    List<TimesheetAccessResponseDTO> getPendingRequests();

    List<TimesheetAccessResponseDTO> getRequestsByPerson(Long personId);
}