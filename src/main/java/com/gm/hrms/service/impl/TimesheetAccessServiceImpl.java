package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TimesheetAccessRequestDTO;
import com.gm.hrms.dto.request.TimesheetAccessReviewDTO;
import com.gm.hrms.dto.response.TimesheetAccessResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.TimesheetAccessRequest;
import com.gm.hrms.enums.TimesheetAccessStatus;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.repository.TimesheetAccessRequestRepository;
import com.gm.hrms.service.EmailService;
import com.gm.hrms.service.TimesheetAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimesheetAccessServiceImpl implements TimesheetAccessService {

    private final TimesheetAccessRequestRepository accessRepo;
    private final PersonalInformationRepository    personRepo;
    private final EmailService                     emailService;

    @Value("${hrms.hr.email:hr@company.com}")
    private String hrEmail;

    @Override
    public TimesheetAccessResponseDTO requestAccess(TimesheetAccessRequestDTO dto) {

        PersonalInformation person = personRepo.findById(dto.getPersonId())
                .orElseThrow(() -> new RuntimeException("Person not found"));

        // Prevent duplicate pending requests for same person/date/type
        accessRepo.findByPerson_IdAndRequestedDateAndAccessTypeAndStatus(
                dto.getPersonId(), dto.getRequestedDate(),
                dto.getAccessType(), TimesheetAccessStatus.PENDING
        ).ifPresent(existing -> {
            throw new RuntimeException(
                    "A pending access request for this date already exists. " +
                            "Please wait for Admin/HR review.");
        });

        TimesheetAccessRequest request = TimesheetAccessRequest.builder()
                .person(person)
                .requestedDate(dto.getRequestedDate())
                .accessType(dto.getAccessType())
                .status(TimesheetAccessStatus.PENDING)
                .reason(dto.getReason())
                .build();

        TimesheetAccessRequest saved = accessRepo.save(request);

        // Notify HR
        emailService.sendAccessRequestNotification(
                hrEmail,
                person.getFirstName() + " " + person.getLastName(),
                dto.getRequestedDate().toString(),
                dto.getAccessType().name(),
                dto.getReason()
        );

        return toResponse(saved);
    }

    @Override
    public TimesheetAccessResponseDTO reviewRequest(Long requestId,
                                                    TimesheetAccessReviewDTO dto) {

        TimesheetAccessRequest request = accessRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Access request not found"));

        if (request.getStatus() != TimesheetAccessStatus.PENDING) {
            throw new RuntimeException("Request has already been reviewed");
        }

        PersonalInformation reviewer = personRepo.findById(dto.getReviewedById())
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        request.setStatus(dto.getStatus());
        request.setReviewedBy(reviewer);
        request.setReviewedAt(LocalDateTime.now());
        request.setRemarks(dto.getRemarks());

        if (dto.getStatus() == TimesheetAccessStatus.APPROVED) {
            // Access window: 24 hours from approval
            request.setAccessExpiresAt(LocalDateTime.now().plusHours(24));
        }

        return toResponse(accessRepo.save(request));
    }

    @Override
    public List<TimesheetAccessResponseDTO> getPendingRequests() {
        return accessRepo.findByStatusOrderByCreatedAtAsc(TimesheetAccessStatus.PENDING)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TimesheetAccessResponseDTO> getRequestsByPerson(Long personId) {
        return accessRepo.findByPerson_IdOrderByCreatedAtDesc(personId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Mapper ────────────────────────────────────────────────────────────

    private TimesheetAccessResponseDTO toResponse(TimesheetAccessRequest r) {
        return TimesheetAccessResponseDTO.builder()
                .id(r.getId())
                .personId(r.getPerson().getId())
                .personName(r.getPerson().getFirstName() + " " + r.getPerson().getLastName())
                .requestedDate(r.getRequestedDate())
                .accessType(r.getAccessType())
                .status(r.getStatus())
                .reason(r.getReason())
                .remarks(r.getRemarks())
                .accessExpiresAt(r.getAccessExpiresAt())
                .reviewedAt(r.getReviewedAt())
                .build();
    }
}