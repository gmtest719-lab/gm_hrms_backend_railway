package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.OfficeTimingRequestDTO;
import com.gm.hrms.dto.response.OfficeTimingResponseDTO;
import com.gm.hrms.entity.OfficeTiming;
import com.gm.hrms.repository.OfficeTimingRepository;
import com.gm.hrms.service.OfficeTimingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OfficeTimingServiceImpl implements OfficeTimingService {

    private final OfficeTimingRepository repo;

    @Override
    public OfficeTimingResponseDTO createOrUpdate(OfficeTimingRequestDTO dto) {

        OfficeTiming timing =
                repo.findFirstByOrderByIdAsc()
                        .orElse(new OfficeTiming());

        timing.setStartTime(dto.getStartTime());
        timing.setLateThresholdMinutes(dto.getLateThresholdMinutes());

        return map(repo.save(timing));
    }

    @Override
    public OfficeTimingResponseDTO get() {

        OfficeTiming timing =
                repo.findFirstByOrderByIdAsc()
                        .orElseThrow(() ->
                                new RuntimeException("Office timing not set"));

        return map(timing);
    }

    private OfficeTimingResponseDTO map(OfficeTiming t){
        return OfficeTimingResponseDTO.builder()
                .id(t.getId())
                .startTime(t.getStartTime())
                .lateThresholdMinutes(t.getLateThresholdMinutes())
                .build();
    }
}

