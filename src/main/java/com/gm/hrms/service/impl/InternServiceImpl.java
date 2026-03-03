package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternRequestDTO;
import com.gm.hrms.dto.request.InternUpdateDTO;
import com.gm.hrms.dto.response.InternResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.entity.Designation;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.InternStatus;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.InternMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.*;
import com.gm.hrms.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InternServiceImpl implements InternService {

    private final InternRepository internRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final PersonalInformationRepository personalRepository;

    private final AuthService authService;
    private final EmailService emailService;

    private final PersonalInformationService personalInformationService;

    private final InternCollegeService collegeService;
    private final InternshipDetailsService internshipService;
    private final InternMentorService mentorService;
    @Override
    public UserCreateResponseDTO create(
            InternRequestDTO dto,
            Long personalId) {

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Designation desig = designationRepository.findById(dto.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        PersonalInformation person = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal not found"));

        Intern intern = new Intern();
        intern.setInternCode(generateCode());
        intern.setDepartment(dept);
        intern.setDesignation(desig);
        intern.setPersonalInformation(person);
        intern.setStatus(dto.getStatus());

        intern = internRepository.save(intern);

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
                .code(intern.getInternCode())
                .role(RoleType.INTERN)
                .departmentName(dept.getName())
                .createdAt(intern.getCreatedAt())
                .build();
    }

    @Override
    public InternResponseDTO update(Long id, InternUpdateDTO dto) {

        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intern not found"));

        PersonalInformation p = intern.getPersonalInformation();

        // ================= PERSONAL (DELEGATED) =================

        if (dto.getPersonalInformation() != null) {
            personalInformationService.update(
                    p.getId(),
                    dto.getPersonalInformation()
            );
        }

        // ================= CORE =================

        if (dto.getInternCode() != null)
            intern.setInternCode(dto.getInternCode());

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            intern.setDepartment(dept);
        }

        if (dto.getDesignationId() != null) {
            Designation desig = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
            intern.setDesignation(desig);
        }

        if (dto.getStatus() != null)
            intern.setStatus(dto.getStatus());

        // ================= COLLEGE =================

        if (dto.getCollegeDetails() != null)
            collegeService.saveOrUpdate(intern, dto.getCollegeDetails());

        // ================= INTERNSHIP =================

        if (dto.getInternshipDetails() != null)
            internshipService.saveOrUpdate(intern, dto.getInternshipDetails());

        // ================= MENTOR =================

        if (dto.getMentorDetails() != null)
            mentorService.saveOrUpdate(intern, dto.getMentorDetails());

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
    public List<InternResponseDTO> getAll() {

        return internRepository.findAll()
                .stream()
                .map(InternMapper::toResponse)
                .toList();
    }
    @Override
    public void delete(Long id) {

        Intern intern = internRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intern not found"));

        intern.setStatus(InternStatus.INACTIVE);

        // optional: also deactivate personal
        intern.getPersonalInformation().setActive(false);
    }

    private String generateCode() {
        Long count = internRepository.count() + 1;
        return String.format("GMIN%03d", count);
    }
}