package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveEligibilityRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEligibilityRuleResponseDTO;

import java.util.List;

public interface LeaveEligibilityRuleService {

    LeaveEligibilityRuleResponseDTO create(LeaveEligibilityRuleRequestDTO dto);

    LeaveEligibilityRuleResponseDTO getByPolicy(Long policyId);

    LeaveEligibilityRuleResponseDTO update(Long id, LeaveEligibilityRuleRequestDTO dto);

     List<LeaveEligibilityRuleResponseDTO> getAll();

    void delete(Long id);
}