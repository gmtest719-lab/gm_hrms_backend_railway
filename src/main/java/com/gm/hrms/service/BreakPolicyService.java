package com.gm.hrms.service;

import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;

import java.util.List;

public interface BreakPolicyService {

    BreakPolicyResponseDTO create(BreakPolicyRequestDTO dto);

    BreakPolicyResponseDTO update(Long id, BreakPolicyRequestDTO dto);

    BreakPolicyResponseDTO getById(Long id);

    List<BreakPolicyResponseDTO> getAll();

    void delete(Long id);
}