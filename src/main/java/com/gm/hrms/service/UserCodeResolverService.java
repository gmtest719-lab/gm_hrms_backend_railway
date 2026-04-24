package com.gm.hrms.service;

import com.gm.hrms.dto.response.UserCodeDTO;

import java.util.List;
import java.util.Map;

public interface UserCodeResolverService {
    String getCode(Long personalId);

    UserCodeDTO resolve(Long personalInformationId);

    // Kept for bulk use — returns map of personalId → UserCodeDTO
    Map<Long, UserCodeDTO> resolveAll(List<Long> personalInformationIds);
}