package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternInternshipRequestDTO;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternInternshipDetails;
import com.gm.hrms.entity.InternshipDomain;
import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.exception.InvalidRequestException;
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

        // ================= DOMAIN =================

        if (dto.getDomainId() != null) {
            InternshipDomain domain = domainRepository.findById(dto.getDomainId())
                    .orElseThrow(() -> new ResourceNotFoundException("Internship domain not found"));
            details.setDomain(domain);
        }

        // ================= DATES =================

        if (dto.getStartDate() != null)
            details.setStartDate(dto.getStartDate());

        if (dto.getEndDate() != null)
            details.setEndDate(dto.getEndDate());

        // ✅ Date validation
        if (dto.getStartDate() != null && dto.getEndDate() != null &&
                dto.getEndDate().isBefore(dto.getStartDate())) {

            throw new InvalidRequestException("End date cannot be before start date");
        }

        // ================= TRAINING PERIOD =================

        if (dto.getTrainingPeriodMonths() != null)
            details.setTrainingPeriodMonths(dto.getTrainingPeriodMonths());

        // ================= INTERNSHIP TYPE =================

        InternShipType type = dto.getInternshipType() != null
                ? dto.getInternshipType()
                : details.getInternshipType();

        // ================= STIPEND VALIDATION =================

        if (type == InternShipType.PAID &&
                (dto.getStipend() == null && details.getStipend() == null)) {

            throw new InvalidRequestException("Stipend is required for paid internship");
        }

        // ================= SET VALUES =================

        if (dto.getStipend() != null)
            details.setStipend(dto.getStipend());

        if (dto.getInternshipType() != null)
            details.setInternshipType(dto.getInternshipType());

        repository.save(details);
    }
}