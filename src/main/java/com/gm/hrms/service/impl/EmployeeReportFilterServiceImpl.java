package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.EmployeeFilterOptionsDTO;
import com.gm.hrms.dto.response.FilterOptionDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.Gender;
import com.gm.hrms.mapper.EmployeeReportMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.EmployeeReportFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeReportFilterServiceImpl implements EmployeeReportFilterService {

    private final DepartmentRepository          departmentRepository;
    private final DesignationRepository         designationRepository;
    private final BranchRepository              branchRepository;
    private final PersonalInformationRepository personalRepository;

    @Override
    public EmployeeFilterOptionsDTO getAllFilterOptions() {

        List<FilterOptionDTO> departments = departmentRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getStatus()))
                .map(d -> FilterOptionDTO.builder()
                        .id(d.getId()).label(d.getName()).build())
                .toList();

        List<FilterOptionDTO> designations = designationRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getActive()))
                .map(d -> FilterOptionDTO.builder()
                        .id(d.getId()).label(d.getName()).build())
                .toList();

        List<FilterOptionDTO> branches = branchRepository.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getActive()))
                .map(b -> FilterOptionDTO.builder()
                        .id(b.getId()).label(b.getBranchName()).build())
                .toList();

        List<FilterOptionDTO> employees = personalRepository.findAllActive().stream()
                .map(p -> FilterOptionDTO.builder()
                        .id(p.getId())
                        .label(EmployeeReportMapper.fullName(p))
                        .build())
                .toList();

        return EmployeeFilterOptionsDTO.builder()
                .departments(departments)
                .designations(designations)
                .branches(branches)
                .employees(employees)
                .activeOptions(List.of(true, false))
                .genders(Arrays.asList(Gender.values()))
                .build();
    }
}