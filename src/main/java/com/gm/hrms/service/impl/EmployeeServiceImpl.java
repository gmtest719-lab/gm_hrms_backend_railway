package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.EmployeeStatus;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.EmployeeMapper;
import com.gm.hrms.repository.DepartmentRepository;
import com.gm.hrms.repository.DesignationRepository;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.service.*;
import com.gm.hrms.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final AuthService authService;
    private final EmployeeAddressService employeeAddressService;
    private final EmailService emailService;
    private final EmployeeEmploymentService employeeEmploymentService;
    private final EmployeeBankDetailsService employeeBankDetailsService;
    private final PersonalInformationRepository personalInformationRepository;

    // =====================================================
    // ================= CREATE EMPLOYEE ===================
    // =====================================================

    @Override
    public UserCreateResponseDTO create(EmployeeRequestDTO dto,
                                        Long personalInformationId) {

        //  Fetch Department & Designation
        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Designation desig = designationRepository.findById(dto.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        // 2️⃣ Reporting Manager (Optional)
        Employee reportingManager = null;
        if (dto.getReportingManagerId() != null) {
            reportingManager = employeeRepository.findById(dto.getReportingManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reporting manager not found"));
        }

        // 🔥 3️⃣ FETCH PERSONAL INFORMATION
        PersonalInformation person =
                personalInformationRepository.findById(personalInformationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Personal information not found"));

        // 4️⃣ Generate Employee Code
        String autoCode = generateEmployeeCode();

        // 5️⃣ Create Employee Core
        Employee employee = EmployeeMapper.toEntity(
                dto,
                dept,
                desig,
                reportingManager,
                autoCode
        );

        // 🔥 VERY IMPORTANT LINE
        employee.setPersonalInformation(person);

        employee = employeeRepository.save(employee);

        // =====================================================
        // 🔐 AUTH CREATION
        // =====================================================

        String rawPassword = PasswordGenerator.generatePassword(8);

        authService.createAuthForPerson(
                person, // use person directly
                employee.getRole(),
                rawPassword
        );

        emailService.sendCredentials(
                username(person),
                autoCode,
                rawPassword
        );

        // =====================================================
        // HR MODULES
        // =====================================================

        if (dto.getEmployment() != null) {
            employeeEmploymentService.saveOrUpdate(employee, dto.getEmployment());
        }

        if (dto.getBankDetails() != null) {
            employeeBankDetailsService.saveOrUpdate(employee, dto.getBankDetails());
        }

        if (dto.getAddress() != null) {
            employeeAddressService.saveOrUpdate(employee, dto.getAddress());
        }

        return UserCreateResponseDTO.builder()
                .personalInformationId(person.getId())
                .employeeId(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .fullName(
                        person.getFirstName() + " " + person.getLastName()
                )
                .role(employee.getRole())
                .departmentName(dept.getName())
                .active(person.getActive())
                .createdAt(employee.getCreatedAt())
                .build();
    }
    // =====================================================
    // ================= UPDATE EMPLOYEE ===================
    // =====================================================

    @Override
    public EmployeeResponseDTO update(Long id, EmployeeUpdateDTO dto) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        PersonalInformation person = employee.getPersonalInformation();

        // =====================================================
        // 🔹 PERSONAL INFORMATION UPDATE
        // =====================================================

        if (dto.getFirstName() != null)
            person.setFirstName(dto.getFirstName());

        if (dto.getMiddleName() != null)
            person.setMiddleName(dto.getMiddleName());

        if (dto.getLastName() != null)
            person.setLastName(dto.getLastName());

        if (dto.getGender() != null)
            person.setGender(dto.getGender());

        if (dto.getDateOfBirth() != null)
            person.setDateOfBirth(dto.getDateOfBirth());

        if (dto.getMaritalStatus() != null)
            person.setMaritalStatus(dto.getMaritalStatus());

        if (dto.getSpouseOrParentName() != null)
            person.setSpouseOrParentName(dto.getSpouseOrParentName());

        if (dto.getActive() != null)
            person.setActive(dto.getActive());

        // =====================================================
        // 🔹 EMPLOYEE CORE UPDATE
        // =====================================================

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            employee.setDepartment(dept);
        }

        if (dto.getDesignationId() != null) {
            Designation desig = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
            employee.setDesignation(desig);
        }

        if (dto.getRole() != null)
            employee.setRole(dto.getRole());

        // =====================================================
        // 🔹 REPORTING MANAGER UPDATE
        // =====================================================

        if (dto.getReportingManagerId() != null) {
            Employee reportingManager = employeeRepository
                    .findById(dto.getReportingManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reporting manager not found"));

            employee.setReportingManager(reportingManager);
        }

        // =====================================================
        // 🔹 HR MODULES UPDATE
        // =====================================================

        if (dto.getEmployment() != null) {
            employeeEmploymentService.saveOrUpdate(employee, dto.getEmployment());
        }

        if (dto.getBankDetails() != null) {
            employeeBankDetailsService.saveOrUpdate(employee, dto.getBankDetails());
        }

        if (dto.getAddress() != null) {
            employeeAddressService.saveOrUpdate(employee, dto.getAddress());
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
    public List<EmployeeResponseDTO> getAll() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    // =====================================================
    // ================= SOFT DELETE =======================
    // =====================================================

    @Override
    public void delete(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getEmployment() != null) {
            employee.getEmployment().setEmployeeStatus(EmployeeStatus.INACTIVE);
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