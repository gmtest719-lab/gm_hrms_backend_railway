package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.Gender;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.EmployeeReportMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.EmployeeReportService;
import com.gm.hrms.service.UserCodeResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeReportServiceImpl implements EmployeeReportService {

    private final EmployeeReportRepository    reportRepository;
    private final DepartmentRepository        departmentRepository;
    private final DesignationRepository       designationRepository;
    private final PersonalInformationRepository personalRepository;
    private final EmployeeRepository          employeeRepository;
    private final UserCodeResolverService     codeResolver;

    // ══════════════════════════════════════════════════════════
    // 1. MASTER REPORT
    // ══════════════════════════════════════════════════════════
    @Override
    public EmployeeReportResponseDTO<EmployeeMasterReportDTO> getMasterReport(
            EmployeeReportFilterDTO filter,
            Pageable pageable,
            RoleType viewerRole,
            Long viewerPersonalId) {

        Page<PersonalInformation> page = reportRepository.findMasterReport(
                safeId(filter.getPersonalInformationId()),
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId()),
                safeId(filter.getBranchId()),
                filter.getActive(),
                filter.getGender(),
                pageable
        );

        List<EmployeeMasterReportDTO> content = page.getContent().stream()
                .map(p -> {
                    boolean isSelf = p.getId().equals(viewerPersonalId);
                    String code = codeResolver.getCode(p.getId());
                    return EmployeeReportMapper.toMasterDTO(p, code, viewerRole, isSelf);
                })
                .toList();

        return EmployeeReportResponseDTO.<EmployeeMasterReportDTO>builder()
                .summary(buildSummary(page.getContent()))
                .data(toPage(content, page))
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // 2. DEPARTMENT-WISE REPORT
    // ══════════════════════════════════════════════════════════
    @Override
    public List<DepartmentWiseEmployeeReportDTO> getDepartmentWiseReport(
            EmployeeReportFilterDTO filter) {

        List<Department> departments = (filter.getDepartmentId() != null)
                ? List.of(departmentRepository.findById(filter.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found")))
                : departmentRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getStatus()))
                .toList();

        return departments.stream().map(dept -> {
            List<PersonalInformation> employees =
                    reportRepository.findByDepartment(dept.getId(), filter.getActive());

            List<EmployeeDirectoryDTO> dtos = employees.stream()
                    .map(p -> EmployeeReportMapper.toDirectoryDTO(
                            p, codeResolver.getCode(p.getId()), RoleType.ADMIN))
                    .toList();

            long activeCount   = employees.stream().filter(p -> Boolean.TRUE.equals(p.getActive())).count();
            long inactiveCount = employees.size() - activeCount;

            return DepartmentWiseEmployeeReportDTO.builder()
                    .departmentId(dept.getId())
                    .departmentName(dept.getName())
                    .departmentCode(dept.getCode())
                    .totalEmployees(employees.size())
                    .activeCount(activeCount)
                    .inactiveCount(inactiveCount)
                    .employees(dtos)
                    .build();
        }).toList();
    }

    // ══════════════════════════════════════════════════════════
    // 3. DESIGNATION-WISE REPORT
    // ══════════════════════════════════════════════════════════
    @Override
    public List<DesignationWiseEmployeeReportDTO> getDesignationWiseReport(
            EmployeeReportFilterDTO filter) {

        List<Designation> designations = (filter.getDesignationId() != null)
                ? List.of(designationRepository.findById(filter.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found")))
                : designationRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getActive()))
                .toList();

        return designations.stream().map(des -> {
            List<PersonalInformation> employees =
                    reportRepository.findByDesignation(des.getId(), filter.getActive());

            List<EmployeeDirectoryDTO> dtos = employees.stream()
                    .map(p -> EmployeeReportMapper.toDirectoryDTO(
                            p, codeResolver.getCode(p.getId()), RoleType.ADMIN))
                    .toList();

            long activeCount   = employees.stream().filter(p -> Boolean.TRUE.equals(p.getActive())).count();
            long inactiveCount = employees.size() - activeCount;

            return DesignationWiseEmployeeReportDTO.builder()
                    .designationId(des.getId())
                    .designationName(des.getName())
                    .totalEmployees(employees.size())
                    .activeCount(activeCount)
                    .inactiveCount(inactiveCount)
                    .employees(dtos)
                    .build();
        }).toList();
    }

    // ══════════════════════════════════════════════════════════
    // 4. JOINING REPORT
    // ══════════════════════════════════════════════════════════
    @Override
    public EmployeeReportResponseDTO<EmployeeJoiningReportDTO> getJoiningReport(
            EmployeeReportFilterDTO filter, Pageable pageable) {

        LocalDate from = filter.getFromDate() != null
                ? filter.getFromDate() : LocalDate.of(2000, 1, 1);
        LocalDate to   = filter.getToDate()   != null
                ? filter.getToDate()   : LocalDate.now();

        Page<PersonalInformation> page = reportRepository.findJoiningReport(
                from,
                to,
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId()),
                safeId(filter.getBranchId()),
                filter.getGender(),
                pageable
        );

        List<EmployeeJoiningReportDTO> content = page.getContent().stream()
                .map(p -> EmployeeReportMapper.toJoiningDTO(p, codeResolver.getCode(p.getId())))
                .toList();

        EmployeeReportSummaryDTO summary = EmployeeReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .newJoinees(page.getTotalElements())
                .build();

        return EmployeeReportResponseDTO.<EmployeeJoiningReportDTO>builder()
                .summary(summary)
                .data(toPage(content, page))
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // 5. EXIT / ATTRITION REPORT
    // ══════════════════════════════════════════════════════════
    @Override
    public EmployeeReportResponseDTO<EmployeeExitReportDTO> getExitReport(
            EmployeeReportFilterDTO filter, Pageable pageable) {

        Page<PersonalInformation> page = reportRepository.findExitReport(
                filter.getFromDate(),
                filter.getToDate(),
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId()),
                pageable
        );

        List<EmployeeExitReportDTO> content = page.getContent().stream()
                .map(p -> EmployeeReportMapper.toExitDTO(p, codeResolver.getCode(p.getId())))
                .toList();

        EmployeeReportSummaryDTO summary = EmployeeReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .totalExits(page.getTotalElements())
                .build();

        return EmployeeReportResponseDTO.<EmployeeExitReportDTO>builder()
                .summary(summary)
                .data(toPage(content, page))
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // 6. STATUS REPORT
    // ══════════════════════════════════════════════════════════
    @Override
    public EmployeeReportResponseDTO<EmployeeStatusReportDTO> getStatusReport(
            EmployeeReportFilterDTO filter, Pageable pageable) {

        Page<PersonalInformation> page = reportRepository.findStatusReport(
                filter.getActive(),
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId()),
                pageable
        );

        List<EmployeeStatusReportDTO> content = page.getContent().stream()
                .map(p -> EmployeeReportMapper.toStatusDTO(p, codeResolver.getCode(p.getId())))
                .toList();

        long active   = page.getContent().stream().filter(p -> Boolean.TRUE.equals(p.getActive())).count();
        long inactive = page.getContent().size() - active;

        EmployeeReportSummaryDTO summary = EmployeeReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .totalActive(active)
                .totalInactive(inactive)
                .build();

        return EmployeeReportResponseDTO.<EmployeeStatusReportDTO>builder()
                .summary(summary)
                .data(toPage(content, page))
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // 7. DIVERSITY REPORT
    // ══════════════════════════════════════════════════════════
    @Override
    public DiversityReportDTO getDiversityReport(EmployeeReportFilterDTO filter) {

        List<PersonalInformation> all = reportRepository.findForDiversity(
                safeId(filter.getDepartmentId()),
                filter.getActive()
        );

        long total   = all.size();
        long male    = all.stream().filter(p -> p.getGender() == Gender.MALE).count();
        long female  = all.stream().filter(p -> p.getGender() == Gender.FEMALE).count();
        long other   = total - male - female;

        long empCount     = all.stream().filter(p -> p.getEmploymentType() == EmploymentType.EMPLOYEE).count();
        long traineeCount = all.stream().filter(p -> p.getEmploymentType() == EmploymentType.TRAINEE).count();
        long internCount  = all.stream().filter(p -> p.getEmploymentType() == EmploymentType.INTERN).count();

        Map<String, Long> byDept = all.stream()
                .filter(p -> p.getWorkProfile() != null && p.getWorkProfile().getDepartment() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getWorkProfile().getDepartment().getName(),
                        Collectors.counting()));

        double malePercent   = total > 0 ? Math.round((male   * 100.0 / total) * 100.0) / 100.0 : 0;
        double femalePercent = total > 0 ? Math.round((female * 100.0 / total) * 100.0) / 100.0 : 0;
        double otherPercent  = total > 0 ? Math.round((other  * 100.0 / total) * 100.0) / 100.0 : 0;

        return DiversityReportDTO.builder()
                .totalEmployees(total)
                .maleCount(male)
                .femaleCount(female)
                .otherGenderCount(other)
                .employeeCount(empCount)
                .traineeCount(traineeCount)
                .internCount(internCount)
                .byDepartment(byDept)
                .malePercent(malePercent)
                .femalePercent(femalePercent)
                .otherPercent(otherPercent)
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // 8. DIRECTORY REPORT (limited fields — all roles)
    // ══════════════════════════════════════════════════════════
    @Override
    public EmployeeReportResponseDTO<EmployeeDirectoryDTO> getDirectoryReport(
            EmployeeReportFilterDTO filter, Pageable pageable, RoleType viewerRole) {

        Page<PersonalInformation> page = reportRepository.findDirectory(
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId()),
                safeId(filter.getBranchId()),
                filter.getGender(),
                pageable
        );

        List<EmployeeDirectoryDTO> content = page.getContent().stream()
                .map(p -> EmployeeReportMapper.toDirectoryDTO(
                        p, codeResolver.getCode(p.getId()), viewerRole))
                .toList();

        EmployeeReportSummaryDTO summary = EmployeeReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .build();

        return EmployeeReportResponseDTO.<EmployeeDirectoryDTO>builder()
                .summary(summary)
                .data(toPage(content, page))
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // 9. SELF MASTER REPORT
    // ══════════════════════════════════════════════════════════
    @Override
    public EmployeeMasterReportDTO getSelfMasterReport(Long viewerPersonalId, RoleType viewerRole) {

        PersonalInformation p = personalRepository.findById(viewerPersonalId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal information not found"));

        return EmployeeReportMapper.toMasterDTO(
                p, codeResolver.getCode(p.getId()), viewerRole, true);
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════

    private EmployeeReportSummaryDTO buildSummary(List<PersonalInformation> list) {
        long active   = list.stream().filter(p -> Boolean.TRUE.equals(p.getActive())).count();
        long inactive = list.size() - active;
        return EmployeeReportSummaryDTO.builder()
                .totalRecords(list.size())
                .totalActive(active)
                .totalInactive(inactive)
                .totalDepartments(reportRepository.countDistinctDepartments())
                .totalDesignations(reportRepository.countDistinctDesignations())
                .build();
    }

    private <T> PageResponseDTO<T> toPage(List<T> content, Page<?> page) {
        return PageResponseDTO.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private Long safeId(Long id) {
        return id != null ? id : 0L;
    }
}