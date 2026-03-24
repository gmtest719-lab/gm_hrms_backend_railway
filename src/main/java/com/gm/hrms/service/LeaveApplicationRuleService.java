package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveApplicationRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveApplicationRuleResponseDTO;

import java.util.List;

public interface LeaveApplicationRuleService {

    LeaveApplicationRuleResponseDTO create(LeaveApplicationRuleRequestDTO dto);

    LeaveApplicationRuleResponseDTO getByPolicy(Long policyId);

    List<LeaveApplicationRuleResponseDTO> getAll();

    LeaveApplicationRuleResponseDTO patchUpdate(Long id, LeaveApplicationRuleRequestDTO dto);

    void delete(Long id);
}