package com.gm.hrms.service;

import com.gm.hrms.dto.request.CompOffRequestDTO;
import com.gm.hrms.dto.response.CompOffResponseDTO;

import java.util.List;

public interface CompOffRequestService {

    CompOffResponseDTO apply(CompOffRequestDTO dto);

    CompOffResponseDTO approve(Long requestId, Long approverId);

    CompOffResponseDTO reject(Long requestId, Long approverId);

    List<CompOffResponseDTO> getByUser(Long personalId);
}