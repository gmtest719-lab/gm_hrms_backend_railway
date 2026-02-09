package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveApplyRequestDTO;
import com.gm.hrms.dto.response.LeaveResponseDTO;

import java.util.List;

public interface LeaveService {

    LeaveResponseDTO applyLeave(Long employeeId, LeaveApplyRequestDTO dto);

    LeaveResponseDTO approve(Long leaveId);

    LeaveResponseDTO reject(Long leaveId);

    LeaveResponseDTO cancel(Long leaveId);

    List<LeaveResponseDTO> getByEmployee(Long employeeId);

    List<LeaveResponseDTO> getAll();
}
