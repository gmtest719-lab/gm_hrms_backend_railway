package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeaveTypeResponseDTO;

import java.util.List;

public interface LeaveTypeService {

    LeaveTypeResponseDTO create(LeaveTypeRequestDTO dto);

    List<LeaveTypeResponseDTO> getAll();

    void delete(Long id);
}
