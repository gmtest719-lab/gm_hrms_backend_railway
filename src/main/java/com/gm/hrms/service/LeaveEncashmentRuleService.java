package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveEncashmentRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEncashmentRuleResponseDTO;

public interface LeaveEncashmentRuleService {

    LeaveEncashmentRuleResponseDTO create(LeaveEncashmentRuleRequestDTO dto);

    LeaveEncashmentRuleResponseDTO getByPolicy(Long policyId);

    LeaveEncashmentRuleResponseDTO patchUpdate(Long id, LeaveEncashmentRuleRequestDTO dto);

    void delete(Long id);
}