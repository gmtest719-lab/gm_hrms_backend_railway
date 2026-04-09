package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.FilterOptionDTO;
import com.gm.hrms.dto.response.LeaveFilterOptionsDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.LeaveStatus;
import com.gm.hrms.repository.*;
import com.gm.hrms.security.LeaveReportSecurityService;
import com.gm.hrms.service.LeaveReportFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveReportFilterServiceImpl implements LeaveReportFilterService {

    private final DepartmentRepository          departmentRepository;
    private final DesignationRepository         designationRepository;
    private final PersonalInformationRepository personalRepository;
    private final LeaveTypeRepository           leaveTypeRepository;
    private final LeaveReportSecurityService    securityService;

    @Override
    public LeaveFilterOptionsDTO getAllFilterOptions() {

        boolean adminOrHr = securityService.isAdminOrHr();

        List<FilterOptionDTO> employees = adminOrHr
                ? personalRepository.findAllActive().stream()
                .map(p -> FilterOptionDTO.builder().id(p.getId()).label(fullName(p)).build())
                .toList()
                : List.of();

        List<FilterOptionDTO> departments = adminOrHr
                ? departmentRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getStatus()))
                .map(d -> FilterOptionDTO.builder().id(d.getId()).label(d.getName()).build())
                .toList()
                : List.of();

        List<FilterOptionDTO> designations = adminOrHr
                ? designationRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getActive()))
                .map(d -> FilterOptionDTO.builder().id(d.getId()).label(d.getName()).build())
                .toList()
                : List.of();

        List<FilterOptionDTO> leaveTypes = leaveTypeRepository.findByIsActiveTrue().stream()
                .map(lt -> FilterOptionDTO.builder().id(lt.getId()).label(lt.getName()).build())
                .toList();

        return LeaveFilterOptionsDTO.builder()
                .employees(employees)
                .departments(departments)
                .designations(designations)
                .leaveTypes(leaveTypes)
                .leaveStatuses(Arrays.asList(LeaveStatus.values()))
                .build();
    }

    @Override
    public List<FilterOptionDTO> getEmployeesByDepartment(Long departmentId) {
        return personalRepository.findActiveByDepartmentId(departmentId).stream()
                .map(p -> FilterOptionDTO.builder().id(p.getId()).label(fullName(p)).build())
                .toList();
    }

    private String fullName(PersonalInformation p) {
        String mid = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? p.getMiddleName() + " " : "";
        return p.getFirstName() + " " + mid + p.getLastName();
    }
}