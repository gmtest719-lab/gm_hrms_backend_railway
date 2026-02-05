package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.entity.Designation;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.EmployeeMapper;
import com.gm.hrms.repository.DepartmentRepository;
import com.gm.hrms.repository.DesignationRepository;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.service.EmployeeService;
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

    @Override
    public EmployeeResponseDTO create(EmployeeRequestDTO dto) {

        if(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())){
            throw new DuplicateResourceException("Employee code already exists");
        }

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Designation desig = designationRepository.findById(dto.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        Employee employee = EmployeeMapper.toEntity(dto, dept, desig);

        return EmployeeMapper.toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponseDTO update(Long id, EmployeeUpdateDTO dto){

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

        return EmployeeMapper.toResponse(employeeRepository.save(employee));
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

        employeeRepository.delete(e);
    }
}

