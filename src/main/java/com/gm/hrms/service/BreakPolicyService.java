package com.gm.hrms.service;

import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BreakPolicyService {

    BreakPolicyResponseDTO create(BreakPolicyRequestDTO dto);

    BreakPolicyResponseDTO update(Long id, BreakPolicyRequestDTO dto);

    BreakPolicyResponseDTO getById(Long id);

    PageResponseDTO<BreakPolicyResponseDTO> getAll(Pageable pageable);

    void delete(Long id);
}