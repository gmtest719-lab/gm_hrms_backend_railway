package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TraineeRequestDTO;
import com.gm.hrms.dto.request.TraineeUpdateDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.entity.Designation;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.enums.TraineeStatus;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.TraineeMapper;
import com.gm.hrms.repository.DepartmentRepository;
import com.gm.hrms.repository.DesignationRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.repository.TraineeRepository;
import com.gm.hrms.service.*;
import com.gm.hrms.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final PersonalInformationRepository personalRepository;
    private final AuthService authService;
    private final EmailService emailService;
    private final TraineeWorkService traineeWorkService;
    private final TraineeEducationService traineeEducationService;

    @Override
    public UserCreateResponseDTO create(
            TraineeRequestDTO dto,
            Long personalInformationId) {

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Designation desig = designationRepository.findById(dto.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        PersonalInformation person = personalRepository.findById(personalInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal not found"));

        String traineeCode = generateTraineeCode();

        Trainee trainee = new Trainee();
        trainee.setTraineeCode(traineeCode);
        trainee.setDepartment(dept);
        trainee.setDesignation(desig);
        trainee.setStipend(dto.getStipend());
        trainee.setStatus(dto.getStatus());
        trainee.setPersonalInformation(person);

        trainee = traineeRepository.save(trainee);

        // 🔥🔥🔥 THIS WAS MISSING
        traineeWorkService.saveOrUpdate(trainee, dto.getWorkDetails());
        traineeEducationService.saveOrUpdate(trainee, dto.getEducationDetails());

        // 🔐 PASSWORD GENERATION
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
                .departmentName(dept.getName())
                .role(RoleType.TRAINEE)
                .active(person.getActive())
                .createdAt(trainee.getCreatedAt())
                .build();
    }


    @Override
    public TraineeResponseDTO update(Long id, TraineeUpdateDTO dto) {

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        PersonalInformation p = trainee.getPersonalInformation();

        if (dto.getFirstName() != null)
            p.setFirstName(dto.getFirstName());

        if (dto.getLastName() != null)
            p.setLastName(dto.getLastName());

        if (dto.getActive() != null)
            p.setActive(dto.getActive());

        if (dto.getDepartmentId() != null) {
            Department d = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            trainee.setDepartment(d);
        }

        if (dto.getDesignationId() != null) {
            Designation d = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
            trainee.setDesignation(d);
        }

        if (dto.getStipend() != null)
            trainee.setStipend(dto.getStipend());

        if (dto.getStatus() != null)
            trainee.setStatus(dto.getStatus());

        // Work + Education partial update separately
        traineeWorkService.saveOrUpdate(trainee, dto.getWorkDetails());
        traineeEducationService.saveOrUpdate(trainee, dto.getEducationDetails());

        return TraineeMapper.toResponse(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeResponseDTO getById(Long id) {

        return traineeRepository.findById(id)
                .map(TraineeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeResponseDTO> getAll() {

        return traineeRepository.findAll()
                .stream()
                .map(TraineeMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        trainee.setStatus(TraineeStatus.INACTIVE);
    }

    private String generateTraineeCode() {
        Long count = traineeRepository.count() + 1;
        return String.format("GMTR%03d", count);
    }
}