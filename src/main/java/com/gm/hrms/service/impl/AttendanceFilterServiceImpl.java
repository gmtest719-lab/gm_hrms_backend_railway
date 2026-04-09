package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.AttendanceFilterOptionsDTO;
import com.gm.hrms.dto.response.FilterOptionDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.enums.RegularizationStatus;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.AttendanceFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceFilterServiceImpl implements AttendanceFilterService {

    private final DepartmentRepository          departmentRepository;
    private final DesignationRepository         designationRepository;
    private final ShiftRepository               shiftRepository;
    private final BranchRepository              branchRepository;
    private final PersonalInformationRepository personalRepository;

    @Override
    public AttendanceFilterOptionsDTO getAllFilterOptions() {

        List<FilterOptionDTO> departments = departmentRepository.findAll()
                .stream()
                .filter(d -> Boolean.TRUE.equals(d.getStatus())) // active only
                .map(d -> FilterOptionDTO.builder()
                        .id(d.getId())
                        .label(d.getName())
                        .build())
                .toList();

        List<FilterOptionDTO> designations = designationRepository.findAll()
                .stream()
                .filter(d -> Boolean.TRUE.equals(d.getActive()))
                .map(d -> FilterOptionDTO.builder()
                        .id(d.getId())
                        .label(d.getName())
                        .build())
                .toList();

        List<FilterOptionDTO> shifts = shiftRepository.findAll()
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .map(s -> FilterOptionDTO.builder()
                        .id(s.getId())
                        .label(s.getShiftName() + " (" + s.getShiftType().name() + ")")
                        .build())
                .toList();

        List<FilterOptionDTO> branches = branchRepository.findAll()
                .stream()
                .filter(b -> Boolean.TRUE.equals(b.getActive()))
                .map(b -> FilterOptionDTO.builder()
                        .id(b.getId())
                        .label(b.getBranchName()) // ← branchName not name
                        .build())
                .toList();

        List<FilterOptionDTO> employees = personalRepository.findAllActive()
                .stream()
                .map(p -> FilterOptionDTO.builder()
                        .id(p.getId())
                        .label(fullName(p))
                        .build())
                .toList();

        return AttendanceFilterOptionsDTO.builder()
                .departments(departments)
                .designations(designations)
                .shifts(shifts)
                .branches(branches)
                .employees(employees)
                .attendanceStatuses(Arrays.asList(AttendanceStatus.values()))
                .regularizationStatuses(Arrays.asList(RegularizationStatus.values()))
                .build();
    }

    @Override
    public List<FilterOptionDTO> getDesignationsByDepartment(Long departmentId) {
        // Designation entity has no department relationship,
        // so we return all active designations regardless of departmentId
        return designationRepository.findAll()
                .stream()
                .filter(d -> Boolean.TRUE.equals(d.getActive()))
                .map(d -> FilterOptionDTO.builder()
                        .id(d.getId())
                        .label(d.getName())
                        .build())
                .toList();
    }

    @Override
    public List<FilterOptionDTO> getEmployeesByDepartment(Long departmentId) {
        return personalRepository.findActiveByDepartmentId(departmentId)
                .stream()
                .map(p -> FilterOptionDTO.builder()
                        .id(p.getId())
                        .label(fullName(p))
                        .build())
                .toList();
    }

    private String fullName(PersonalInformation p) {
        String mid = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? p.getMiddleName() + " " : "";
        return p.getFirstName() + " " + mid + p.getLastName();
    }
}