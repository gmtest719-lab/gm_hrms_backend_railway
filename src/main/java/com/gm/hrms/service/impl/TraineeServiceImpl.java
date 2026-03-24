package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TraineeRequestDTO;
import com.gm.hrms.dto.request.TraineeUpdateDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.entity.*;
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

        String traineeCode = generateTraineeCode();

        Trainee trainee = new Trainee();
        trainee.setTraineeCode(traineeCode);
        trainee.setStipend(dto.getStipend());
        trainee.setPersonalInformation(person);

        trainee = traineeRepository.save(trainee);

        if (dto.getStipend() != null && dto.getStipend() < 0) {
            throw new InvalidRequestException("Stipend cannot be negative");
        }
        // WORK + EDUCATION
        if (dto.getWorkDetails() != null)
            traineeWorkService.saveOrUpdate(trainee, dto.getWorkDetails());

        if (dto.getEducationDetails() != null)
            traineeEducationService.saveOrUpdate(trainee, dto.getEducationDetails());

        // AUTH
        String rawPassword = PasswordGenerator.generatePassword(8);

        authService.createAuthForPerson(
                person,
                RoleType.TRAINEE,
                rawPassword
        );

        emailService.sendCredentials(
                person.getContact().getOfficeEmail(),
                traineeCode,
                rawPassword
        );

        return UserCreateResponseDTO.builder()
                .personalInformationId(person.getId())
                .code(traineeCode)
                .id(trainee.getId())
                .fullName(person.getFirstName() + " " + person.getLastName())
                .departmentName(
                        person.getWorkProfile() != null &&
                                person.getWorkProfile().getDepartment() != null
                                ? person.getWorkProfile().getDepartment().getName()
                                : null
                )
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

        // ================= PARSE =================
        TraineeUpdateDTO dto =
                objectMapper.readValue(traineeJson, TraineeUpdateDTO.class);

        // ================= FETCH =================
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        PersonalInformation p = trainee.getPersonalInformation();

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

        // ================= TRAINEE CODE =================
        if (dto.getTraineeCode() != null &&
                !dto.getTraineeCode().equals(trainee.getTraineeCode())) {

            if (dto.getTraineeCode().trim().isEmpty()) {
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

        // ================= STIPEND =================
        if (dto.getStipend() != null) {

            if (dto.getStipend() < 0) {
                throw new InvalidRequestException("Stipend cannot be negative");
            }

            trainee.setStipend(dto.getStipend());
        }

        // ================= WORK =================
        if (dto.getWorkDetails() != null) {
            traineeWorkService.saveOrUpdate(trainee, dto.getWorkDetails());
        }

        // ================= EDUCATION =================
        if (dto.getEducationDetails() != null) {
            traineeEducationService.saveOrUpdate(trainee, dto.getEducationDetails());
        }

        // ================= DOCUMENT =================
        if (documents != null || reasons != null) {

            documentService.updateDocuments(
                    p.getId(),
                    p.getEmploymentType(),
                    documents,
                    reasons
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
}