package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.*;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final PersonalDocumentService documentService;
    private final PersonalInformationService personalService;
    private final EmployeeService employeeService;
    private final InternService internService;
    private final TraineeService traineeService;
    private final ObjectMapper mapper;
    private final FileStorageService fileStorageService;

    @Override
    public UserCreateResponseDTO create(
            String personalInformationJson,
            String internJson,
            String employeeJson,
            String traineeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception {

        // ================= PARSE =================

        PersonalInformationRequestDTO personalInformation =
                mapper.readValue(personalInformationJson, PersonalInformationRequestDTO.class);

        InternRequestDTO intern = internJson != null
                ? mapper.readValue(internJson, InternRequestDTO.class)
                : null;

        EmployeeRequestDTO employee = employeeJson != null
                ? mapper.readValue(employeeJson, EmployeeRequestDTO.class)
                : null;

        TraineeRequestDTO trainee = traineeJson != null
                ? mapper.readValue(traineeJson, TraineeRequestDTO.class)
                : null;

        // ================= PROFILE IMAGE =================

        if (profileImage == null || profileImage.isEmpty()) {
            throw new InvalidRequestException("Profile image is required");
        }

        String profileImagePath = fileStorageService.save(profileImage);

        //  SET IN DTO
        personalInformation.setProfileImageUrl(profileImagePath);

        // ================= PERSONAL =================

        PersonalInformationResponseDTO person =
                personalService.create(personalInformation);

        EmploymentType type = personalInformation.getEmploymentType();

        // ================= DOCUMENT =================

        documentService.validateAndSaveDocuments(
                person.getId(),
                type,
                documents,
                reasons
        );

        // ================= ROUTING =================

        return switch (type) {

            case EMPLOYEE -> {
                if (employee == null)
                    throw new InvalidRequestException("Employee data required");
                yield employeeService.create(employee, person.getId());
            }

            case INTERN -> {
                if (intern == null)
                    throw new InvalidRequestException("Intern data required");
                yield internService.create(intern, person.getId());
            }

            case TRAINEE -> {
                if (trainee == null)
                    throw new InvalidRequestException("Trainee data required");
                yield traineeService.create(trainee, person.getId());
            }
        };
    }


}