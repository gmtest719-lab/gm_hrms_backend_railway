package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.enums.Status;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.EmployeeMapper;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
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
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthService authService;
    private final EmailService emailService;
    private final EmployeeEmploymentService employeeEmploymentService;
    private final PersonalInformationRepository personalInformationRepository;
    private final PersonalInformationService personalInformationService;
    private final PersonalDocumentService documentService;
    private final ObjectMapper objectMapper;
    private final FileStorageService fileStorageService;

    // =====================================================
    // ================= CREATE EMPLOYEE ===================
    // =====================================================

    @Override
    public UserCreateResponseDTO create(EmployeeRequestDTO dto,
                                        Long personalInformationId) {

        PersonalInformation person =
                personalInformationRepository.findById(personalInformationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Personal information not found"));

        boolean isDraft = person.getRecordStatus() == RecordStatus.DRAFT;

        // ================= VALIDATION (ONLY SUBMIT) =================

        if (!isDraft && dto.getRole() == null) {
            throw new InvalidRequestException("Role is required");
        }

        String autoCode = generateEmployeeCode();

        Employee employee = EmployeeMapper.toEntity(dto, autoCode);
        employee.setPersonalInformation(person);

        employee = employeeRepository.save(employee);

        // ================= AUTH (ONLY IF SUBMITTED & NOT EXISTS) =================

        boolean isSubmitted = person.getRecordStatus() == RecordStatus.SUBMITTED;

        if (isSubmitted && !authService.existsByPerson(person)) {

            String username = username(person);

            String rawPassword = PasswordGenerator.generatePassword(8);

            authService.createAuthForPerson(
                    person,
                    employee.getRole(),
                    rawPassword
            );

            emailService.sendCredentials(
                    username,
                    autoCode,
                    rawPassword
            );
        }

        // ================= EMPLOYMENT =================

        if (dto.getEmployment() != null) {
            employeeEmploymentService.saveOrUpdate(employee, dto.getEmployment());
        }

        return UserCreateResponseDTO.builder()
                .personalInformationId(person.getId())
                .id(employee.getId())
                .code(employee.getEmployeeCode())
                .fullName(person.getFirstName() + " " + person.getLastName())
                .role(employee.getRole())
                .active(person.getActive())
                .createdAt(employee.getCreatedAt())
                .build();
    }

    // =====================================================
    // ================= UPDATE EMPLOYEE ===================
    // =====================================================

    @Override
    public EmployeeResponseDTO update(
            Long id,
            String employeeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception {

        EmployeeUpdateDTO dto =
                objectMapper.readValue(employeeJson, EmployeeUpdateDTO.class);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        PersonalInformation person = employee.getPersonalInformation();

        boolean isDraft = person.getRecordStatus() == RecordStatus.DRAFT;


        // ================= STATUS =================

        boolean isSubmitted = person.getRecordStatus() == RecordStatus.SUBMITTED;



        // ================= VALIDATION =================

        if (!isDraft) {

            String code = dto.getEmployeeCode() != null
                    ? dto.getEmployeeCode()
                    : employee.getEmployeeCode();

            if (code == null || code.isBlank()) {
                throw new InvalidRequestException("Employee code is required");
            }
        }

        // ================= PROFILE IMAGE =================

        if (isSubmitted &&
                (profileImage == null || profileImage.isEmpty()) &&
                (person.getProfileImageUrl() == null ||
                        person.getProfileImageUrl().isBlank())) {

            throw new InvalidRequestException("Profile image is required");
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImagePath = fileStorageService.save(profileImage);
            person.setProfileImageUrl(profileImagePath);
        }

        // ================= PERSONAL =================

        if (dto.getPersonalInformation() != null) {
            personalInformationService.update(
                    person.getId(),
                    dto.getPersonalInformation()
            );
        }

        // ================= EMPLOYEE CODE =================

        if (dto.getEmployeeCode() != null &&
                !dto.getEmployeeCode().equals(employee.getEmployeeCode())) {

            if (!isDraft && dto.getEmployeeCode().trim().isEmpty()) {
                throw new InvalidRequestException("Employee code cannot be blank");
            }

            boolean exists = employeeRepository
                    .existsByEmployeeCodeAndIdNot(dto.getEmployeeCode(), id);

            if (exists) {
                throw new InvalidRequestException(
                        "Employee code already exists: " + dto.getEmployeeCode()
                );
            }

            employee.setEmployeeCode(dto.getEmployeeCode());
        }

        // ================= EMPLOYMENT =================

        if (dto.getEmployment() != null) {

            if (!isDraft &&
                    dto.getEmployment().getCtc() != null &&
                    dto.getEmployment().getCtc() <= 0) {

                throw new InvalidRequestException("CTC must be greater than 0");
            }

            employeeEmploymentService.saveOrUpdate(
                    employee,
                    dto.getEmployment()
            );
        }

        // ================= DOCUMENT =================

        if (!isDraft) {

            documentService.validateAndSaveDocuments(
                    person.getId(),
                    person.getEmploymentType(),
                    documents,
                    reasons
            );
        }

        // ================= AUTH TRIGGER (IMPORTANT) =================


        if (isSubmitted && !authService.existsByPerson(person)) {

            String username = username(person);

            String rawPassword = PasswordGenerator.generatePassword(8);

            authService.createAuthForPerson(
                    person,
                    employee.getRole(),
                    rawPassword
            );

            emailService.sendCredentials(
                    username,
                    employee.getEmployeeCode(),
                    rawPassword
            );
        }

        return EmployeeMapper.toResponse(employee);
    }

    // =====================================================
    // ================= FETCH METHODS =====================
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<EmployeeResponseDTO> getAll(Pageable pageable) {

        Page<Employee> page = employeeRepository.findAll(pageable);

        List<EmployeeResponseDTO> content = page.getContent()
                .stream()
                .map(EmployeeMapper::toResponse)
                .toList();

        return PageResponseDTO.<EmployeeResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // =====================================================
    // ================= SOFT DELETE =======================
    // =====================================================

    @Override
    public void delete(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        PersonalInformation person = employee.getPersonalInformation();

        if (person != null && person.getWorkProfile() != null) {
            person.getWorkProfile().setStatus(Status.INACTIVE);
        }
    }

    // =====================================================
    // ================= CODE GENERATOR ====================
    // =====================================================

    private String generateEmployeeCode() {
        Long count = employeeRepository.count() + 1;
        return String.format("GMEP%03d", count);
    }

    private String username(PersonalInformation person) {

        if (person.getContact() != null &&
                person.getContact().getOfficeEmail() != null &&
                !person.getContact().getOfficeEmail().isBlank()) {

            return person.getContact().getOfficeEmail();
        }

        return person.getContact().getPersonalEmail();
    }
}