package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternInternshipRequestDTO;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternInternshipDetails;
import com.gm.hrms.entity.InternshipDomain;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.InternInternshipRepository;
import com.gm.hrms.repository.InternshipDomainRepository;
import com.gm.hrms.service.InternshipDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipDetailsServiceImpl implements InternshipDetailsService {

    private final InternInternshipRepository repository;
    private final InternshipDomainRepository domainRepository;

    @Override
    public void saveOrUpdate(Intern intern,
                             InternInternshipRequestDTO dto) {

        if (dto == null) return;

        InternInternshipDetails details =
                repository.findByIntern(intern)
                        .orElse(new InternInternshipDetails());

        details.setIntern(intern);

        if (dto.getDomainId() != null) {
            InternshipDomain domain = domainRepository.findById(dto.getDomainId())
                    .orElseThrow(() -> new ResourceNotFoundException("Internship domain not found"));
            details.setDomain(domain);
        }

        if (dto.getStartDate() != null)
            details.setStartDate(dto.getStartDate());

        if (dto.getEndDate() != null)
            details.setEndDate(dto.getEndDate());

        if (dto.getShiftTiming() != null)
            details.setShiftTiming(dto.getShiftTiming());

        if (dto.getMode() != null)
            details.setMode(dto.getMode());

        repository.save(details);
    }
}