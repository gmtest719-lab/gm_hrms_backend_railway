package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.entity.Designation;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeContact;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.EmployeeMapper;
import com.gm.hrms.repository.DepartmentRepository;
import com.gm.hrms.repository.DesignationRepository;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.service.*;
import com.gm.hrms.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final AuthService authService;
    private final EmployeeContactService employeeContactService;
    private final EmployeeAddressService employeeAddressService;
    private final EmployeeDocumentService  employeeDocumentService;
    private final EmailService emailService;



    @Override
    @Transactional
    public EmployeeResponseDTO create(EmployeeRequestDTO dto,
                                      List<MultipartFile> documents) {

        //  Duplicate Check
        if(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())){
            throw new DuplicateResourceException("Employee code already exists");
        }

        //  Fetch Relations
        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Designation desig = designationRepository.findById(dto.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        //  Create Employee
        Employee employee = EmployeeMapper.toEntity(dto, dept, desig);
        employee = employeeRepository.save(employee);

        //  Create Contact
        if(dto.getContact() != null){
            EmployeeContact contact= employeeContactService.createContact(employee, dto.getContact());
            employee.setContact(contact);
        }

        // Generate password
        String rawPassword = PasswordGenerator.generatePassword(8);

// Create Auth
        authService.createAuthForEmployee(employee, rawPassword);

// Send email
        emailService.sendCredentials(
                employee.getContact().getOfficeEmail(),
                employee.getFirstName(),
                rawPassword
        );

        //  Create Address
        if(dto.getAddress() != null){
            employeeAddressService.createAddress(employee, dto.getAddress());
        }

        //  Create Documents
        if(documents != null && !documents.isEmpty()){
            employeeDocumentService.saveDocuments(employee, documents);
        }

        //  Return Response
        return EmployeeMapper.toResponse(employee);
    }


    @Override
    @Transactional
    public EmployeeResponseDTO update(Long id,
                                      EmployeeUpdateDTO dto,
                                      List<MultipartFile> documents){

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if(dto.getFirstName()!=null){
            employee.setFirstName(dto.getFirstName());
        }

        if(dto.getLastName()!=null){
            employee.setLastName(dto.getLastName());
        }

        if(dto.getEmployeeCode()!=null){
            employee.setEmployeeCode(dto.getEmployeeCode());
        }

        if(dto.getDepartmentId()!=null){
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            employee.setDepartment(dept);
        }

        if(dto.getDesignationId()!=null){
            Designation desig = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
            employee.setDesignation(desig);
        }

        if(dto.getRole()!=null){
            employee.setRole(dto.getRole());
        }

        if(dto.getContact() != null){
            employeeContactService.updateContact(employee, dto.getContact());
        }

        if(dto.getAddress() != null){
            employeeAddressService.updateAddress(employee, dto.getAddress());
        }


        //  Documents Update (Optional)
        if(documents != null && !documents.isEmpty()){
            employeeDocumentService.saveDocuments(employee, documents);
        }

        return EmployeeMapper.toResponse(employee);
    }


    @Override
    public EmployeeResponseDTO getById(Long id) {

        Employee e = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return EmployeeMapper.toResponse(e);
    }

    @Override
    public List<EmployeeResponseDTO> getAll() {

        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Employee e = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        e.setActive(false); //  mark inactive

        employeeRepository.save(e);
    }
}

