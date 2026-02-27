package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeEmploymentRequestDTO;
import com.gm.hrms.dto.response.EmployeeEmploymentResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeEmployment;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.EmployeeEmploymentRepository;
import com.gm.hrms.service.EmployeeEmploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeEmploymentServiceImpl implements EmployeeEmploymentService {

    private final EmployeeEmploymentRepository repository;

    @Override
    public void saveOrUpdate(Employee employee,
                             EmployeeEmploymentRequestDTO dto) {

        EmployeeEmployment employment =
                repository.findByEmployee(employee)
                        .orElse(new EmployeeEmployment());

        employment.setEmployee(employee);

        if (dto.getDateOfJoining() != null)
            employment.setDateOfJoining(dto.getDateOfJoining());

        if (dto.getYearOfExperience() != null)
            employment.setYearOfExperience(dto.getYearOfExperience());

        if (dto.getCtc() != null)
            employment.setCtc(dto.getCtc());

        if (dto.getPreviousCompanyNames() != null)
            employment.setPreviousCompanyNames(dto.getPreviousCompanyNames());

        if (dto.getWorkMode() != null)
            employment.setWorkMode(dto.getWorkMode());

        if (dto.getEmployeeStatus() != null)
            employment.setEmployeeStatus(dto.getEmployeeStatus());

        if (dto.getNoticePeriod() != null)
            employment.setNoticePeriod(dto.getNoticePeriod());

        if (dto.getShiftTiming() != null)
            employment.setShiftTiming(dto.getShiftTiming());

        if (dto.getBranchName() != null)
            employment.setBranchName(dto.getBranchName());

        repository.save(employment);
    }

    @Override
    public EmployeeEmploymentResponseDTO getByEmployee(Employee employee) {

        EmployeeEmployment employment =
                repository.findByEmployee(employee)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Employment details not found"));

        return mapToResponse(employment);
    }

    private EmployeeEmploymentResponseDTO mapToResponse(EmployeeEmployment employment) {

        return EmployeeEmploymentResponseDTO.builder()
                .dateOfJoining(employment.getDateOfJoining())
                .yearOfExperience(employment.getYearOfExperience())
                .ctc(employment.getCtc())
                .previousCompanyNames(employment.getPreviousCompanyNames())
                .workMode(employment.getWorkMode())
                .employeeStatus(employment.getEmployeeStatus())
                .noticePeriod(employment.getNoticePeriod())
                .shiftTiming(employment.getShiftTiming())
                .branchName(employment.getBranchName())
                .build();
    }
}