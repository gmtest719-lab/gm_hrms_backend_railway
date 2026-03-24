package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternRequestDTO;
import com.gm.hrms.dto.request.InternUpdateDTO;
import com.gm.hrms.dto.response.InternResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.enums.Status;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.InternMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.*;
import com.gm.hrms.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InternServiceImpl implements InternService {

    private final InternRepository internRepository;
    private final PersonalInformationRepository personalRepository;

    private final AuthService authService;
    private final EmailService emailService;

    private final PersonalInformationService personalInformationService;

    private final InternCollegeService collegeService;
    private final InternshipDetailsService internshipService;
    private final InternMentorService mentorService;
    private final PersonalDocumentService documentService;
    private final ObjectMapper objectMapper;
    private final FileStorageService fileStorageService;


    @Override
    public UserCreateResponseDTO create(
            InternRequestDTO dto,
            Long personalId) {


        PersonalInformation person = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal not found"));

        Intern intern = new Intern();
        intern.setInternCode(generateCode());
        intern.setPersonalInformation(person);

        intern = internRepository.save(intern);

        // ================= SAVE COLLEGE =================



        if (dto.getCollegeDetails() != null) {
            collegeService.saveOrUpdate(intern, dto.getCollegeDetails());
        }

        // ================= SAVE INTERNSHIP =================

        if (dto.getInternshipDetails() != null) {
            internshipService.saveOrUpdate(intern, dto.getInternshipDetails());
        }

        // ================= SAVE MENTOR =================

        if (dto.getMentorDetails() != null) {
            mentorService.saveOrUpdate(intern, dto.getMentorDetails());
        }

        // ================= AUTH =================

        String rawPassword = PasswordGenerator.generatePassword(8);

        authService.createAuthForPerson(person, RoleType.INTERN, rawPassword);

        emailService.sendCredentials(
                person.getContact().getOfficeEmail(),
                intern.getInternCode(),
                rawPassword
        );

        return UserCreateResponseDTO.builder()
                .personalInformationId(person.getId())
                .id(intern.getId())
                .active(person.getActive())
                .departmentName(person.getWorkProfile().getDepartment().getName())
                .fullName(person.getFirstName() + " "+ person.getMiddleName()+ " " + person.getLastName())
                .code(intern.getInternCode())
                .role(RoleType.INTERN)
                .createdAt(intern.getCreatedAt())
                .build();
    }

    @Override
    public InternResponseDTO update(
            Long id,
            String internJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception {

        // ================= PARSE =================
        InternUpdateDTO dto =
                objectMapper.readValue(internJson, InternUpdateDTO.class);

        // ================= FETCH =================
        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intern not found"));

        PersonalInformation p = intern.getPersonalInformation();

        // ================= PROFILE IMAGE =================
        if (profileImage != null && !profileImage.isEmpty()) {

            String path = fileStorageService.save(profileImage);
            p.setProfileImageUrl(path);
        }

        // ================= PERSONAL =================
        if (dto.getPersonalInformation() != null) {

            personalInformationService.update(
                    p.getId(),
                    dto.getPersonalInformation()
            );
        }

        // ================= INTERN CODE =================
        if (dto.getInternCode() != null &&
                !dto.getInternCode().equals(intern.getInternCode())) {

            if (dto.getInternCode().trim().isEmpty()) {
                throw new InvalidRequestException("Intern code cannot be blank");
            }

            boolean exists =
                    internRepository.existsByInternCodeAndIdNot(dto.getInternCode(), id);

            if (exists) {
                throw new InvalidRequestException(
                        "Intern code already exists: " + dto.getInternCode()
                );
            }

            intern.setInternCode(dto.getInternCode());
        }

        // ================= COLLEGE =================
        if (dto.getCollegeDetails() != null)
            collegeService.saveOrUpdate(intern, dto.getCollegeDetails());

        // ================= INTERNSHIP =================
        if (dto.getInternshipDetails() != null)
            internshipService.saveOrUpdate(intern, dto.getInternshipDetails());

        // ================= MENTOR =================
        if (dto.getMentorDetails() != null)
            mentorService.saveOrUpdate(intern, dto.getMentorDetails());

        // ================= DOCUMENT =================
        if (documents != null || reasons != null) {

            documentService.updateDocuments(
                    p.getId(),
                    p.getEmploymentType(),
                    documents,
                    reasons
            );
        }

        return InternMapper.toResponse(intern);
    }

    @Override
    @Transactional(readOnly = true)
    public InternResponseDTO getById(Long id) {

        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intern not found"));

        return InternMapper.toResponse(intern);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<InternResponseDTO> getAll(Pageable pageable) {

        Page<Intern> page = internRepository.findAll(pageable);

        List<InternResponseDTO> content = page.getContent()
                .stream()
                .map(InternMapper::toResponse)
                .toList();

        return PageResponseDTO.<InternResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
    @Override
    public void delete(Long id) {

        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intern not found"));

        PersonalInformation person = intern.getPersonalInformation();

        if (person != null && person.getWorkProfile() != null) {
            person.getWorkProfile().setStatus(Status.INACTIVE);
        }
    }


    private String generateCode() {
        Long count = internRepository.count() + 1;
        return String.format("GMIN%03d", count);
    }
}