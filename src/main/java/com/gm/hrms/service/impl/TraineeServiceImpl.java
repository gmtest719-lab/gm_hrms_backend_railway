package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TraineeRequestDTO;
import com.gm.hrms.dto.request.TraineeUpdateDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.enums.Status;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.TraineeMapper;
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
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final PersonalInformationRepository personalRepository;

    private final AuthService authService;
    private final EmailService emailService;

    private final TraineeWorkService traineeWorkService;
    private final TraineeEducationService traineeEducationService;

    private final PersonalInformationService personalInformationService;
    private final PersonalDocumentService documentService;
    private final ObjectMapper objectMapper;
    private final FileStorageService fileStorageService;

    // =====================================================
    // ================= CREATE =============================
    // =====================================================

    @Override
    public UserCreateResponseDTO create(
            TraineeRequestDTO dto,
            Long personalInformationId) {

        PersonalInformation person = personalRepository.findById(personalInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal not found"));

        boolean isDraft = person.getRecordStatus() == RecordStatus.DRAFT;

        // ================= VALIDATION (ONLY SUBMIT) =================

        if (!isDraft) {

            if (dto.getStipend() == null) {
                throw new InvalidRequestException("Stipend is required");
            }

            if (dto.getStipend() < 0) {
                throw new InvalidRequestException("Stipend cannot be negative");
            }
        }

        // ================= CREATE =================

        String traineeCode = generateTraineeCode();

        Trainee trainee = new Trainee();
        trainee.setTraineeCode(traineeCode);
        trainee.setStipend(dto.getStipend());
        trainee.setPersonalInformation(person);

        trainee = traineeRepository.save(trainee);

        // ================= CHILD MODULES =================

        if (dto.getWorkDetails() != null) {
            traineeWorkService.saveOrUpdate(trainee, dto.getWorkDetails());
        }

        if (dto.getEducationDetails() != null) {
            traineeEducationService.saveOrUpdate(trainee, dto.getEducationDetails());
        }

        // ================= AUTH (ONLY IF SUBMITTED) =================

        boolean isSubmitted = person.getRecordStatus() == RecordStatus.SUBMITTED;

        if (isSubmitted && !authService.existsByPerson(person)) {

            String username = username(person);
            String rawPassword = PasswordGenerator.generatePassword(8);

            authService.createAuthForPerson(
                    person,
                    RoleType.TRAINEE,
                    rawPassword
            );

            emailService.sendCredentials(
                    username,
                    traineeCode,
                    rawPassword
            );
        }

        return UserCreateResponseDTO.builder()
                .personalInformationId(person.getId())
                .id(trainee.getId())
                .code(traineeCode)
                .fullName(person.getFirstName() + " " + person.getLastName())
                .role(RoleType.TRAINEE)
                .active(person.getActive())
                .createdAt(trainee.getCreatedAt())
                .build();
    }

    // =====================================================
    // ================= UPDATE =============================
    // =====================================================

    @Override
    public TraineeResponseDTO update(
            Long id,
            String traineeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception {

        TraineeUpdateDTO dto =
                objectMapper.readValue(traineeJson, TraineeUpdateDTO.class);

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        PersonalInformation person = trainee.getPersonalInformation();

        boolean isDraft = person.getRecordStatus() == RecordStatus.DRAFT;

        // ================= PROFILE IMAGE =================


        boolean isSubmitted = person.getRecordStatus() == RecordStatus.SUBMITTED;

//  GET EXISTING FROM DB (update case)
        String existingImage = null;

        if (person.getId() != null) {
            existingImage = personalRepository.findById(person.getId())
                    .map(PersonalInformation::getProfileImageUrl)
                    .orElse(null);
        }

        if (isSubmitted &&
                (profileImage == null || profileImage.isEmpty()) &&
                (existingImage == null || existingImage.isBlank())) {

            throw new InvalidRequestException("Profile image is required");
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String path = fileStorageService.save(profileImage);
            person.setProfileImageUrl(path);
        }






        // ================= PERSONAL =================

        if (dto.getPersonalInformation() != null) {
            personalInformationService.update(
                    person.getId(),
                    dto.getPersonalInformation()
            );
        }

        // ================= CODE =================

        if (dto.getTraineeCode() != null &&
                !dto.getTraineeCode().equals(trainee.getTraineeCode())) {

            if (!isDraft && dto.getTraineeCode().isBlank()) {
                throw new InvalidRequestException("Trainee code cannot be blank");
            }

            boolean exists =
                    traineeRepository.existsByTraineeCodeAndIdNot(dto.getTraineeCode(), id);

            if (exists) {
                throw new InvalidRequestException(
                        "Trainee code already exists: " + dto.getTraineeCode()
                );
            }

            trainee.setTraineeCode(dto.getTraineeCode());
        }

        // ================= MERGE VALIDATION =================

        if (!isDraft) {

            Double stipend = dto.getStipend() != null
                    ? dto.getStipend()
                    : trainee.getStipend();

            if (stipend == null) {
                throw new InvalidRequestException("Stipend is required");
            }

            if (stipend < 0) {
                throw new InvalidRequestException("Stipend cannot be negative");
            }
        }

        // ================= PATCH =================

        if (dto.getStipend() != null) {
            trainee.setStipend(dto.getStipend());
        }

        if (dto.getWorkDetails() != null) {
            traineeWorkService.saveOrUpdate(trainee, dto.getWorkDetails());
        }

        if (dto.getEducationDetails() != null) {
            traineeEducationService.saveOrUpdate(trainee, dto.getEducationDetails());
        }

        // ================= DOCUMENT =================

        if (!isDraft) {
            documentService.updateDocuments(
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
                    RoleType.TRAINEE,
                    rawPassword
            );

            emailService.sendCredentials(
                    username,
                    trainee.getTraineeCode(),
                    rawPassword
            );
        }

        return TraineeMapper.toResponse(trainee);
    }

    // =====================================================
    // ================= GET BY ID =========================
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public TraineeResponseDTO getById(Long id) {

        return traineeRepository.findById(id)
                .map(TraineeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
    }

    // =====================================================
    // ================= GET ALL ===========================
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<TraineeResponseDTO> getAll(Pageable pageable) {

        Page<Trainee> page = traineeRepository.findAll(pageable);

        List<TraineeResponseDTO> content = page.getContent()
                .stream()
                .map(TraineeMapper::toResponse)
                .toList();

        return PageResponseDTO.<TraineeResponseDTO>builder()
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
    // ================= DELETE ============================
    // =====================================================

    @Override
    public void delete(Long id) {

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        PersonalInformation person = trainee.getPersonalInformation();

        if (person != null && person.getWorkProfile() != null) {
            person.getWorkProfile().setStatus(Status.INACTIVE);
        }
    }

    // =====================================================
    // ================= UTIL ==============================
    // =====================================================

    private String generateTraineeCode() {
        Long count = traineeRepository.count() + 1;
        return String.format("GMTR%03d", count);
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