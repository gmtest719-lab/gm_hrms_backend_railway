package com.gm.hrms.service;

import com.gm.hrms.dto.request.OfficeTimingRequestDTO;
import com.gm.hrms.dto.response.OfficeTimingResponseDTO;

public interface OfficeTimingService {

    OfficeTimingResponseDTO createOrUpdate(OfficeTimingRequestDTO dto);

    OfficeTimingResponseDTO get();
}

