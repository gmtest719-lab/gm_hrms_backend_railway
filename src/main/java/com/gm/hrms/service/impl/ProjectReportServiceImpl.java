// com/gm/hrms/service/impl/ProjectReportServiceImpl.java
package com.gm.hrms.service.impl;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.ProjectReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AssigneeType;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.ProjectStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.ProjectReportMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.ProjectReportService;
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
public class ProjectReportServiceImpl implements ProjectReportService {

    private final ProjectReportRepository         projectReportRepository;
    private final ProjectAssignmentRepository     assignmentRepository;
    private final PersonalInformationRepository   personalRepository;
    private final EmployeeRepository              employeeRepository;
    private final TraineeRepository               traineeRepository;
    private final InternRepository                internRepository;
    private final UserCodeResolverService         codeResolver;
    private final TimesheetRepository timesheetRepository;

    // ======================================================
    // 1. PROJECT MASTER REPORT
    // ======================================================
    @Override
    public ProjectReportResponseDTO<ProjectMasterReportDTO> getProjectMasterReport(
            ProjectReportFilterDTO filter, Pageable pageable, CustomUserDetails user) {

        Page<Project> page = isAdminOrHr(user)
                ? projectReportRepository.findProjectsWithFilters(
                filter.getStatus(),
                safeFrom(filter.getFromDate()),
                safeTo(filter.getToDate()),
                safeId(filter.getProjectId()),
                pageable)
                : findAssignedProjectPage(user, filter, pageable);

        List<ProjectMasterReportDTO> content = page.getContent().stream()
                .map(p -> {
                    List<ProjectAssignment> assignments = assignmentRepository
                            .findByProjectId(p.getId(), Pageable.unpaged()).getContent();
                    return buildMasterDTO(p, assignments);
                })
                .toList();

        return ProjectReportResponseDTO.<ProjectMasterReportDTO>builder()
                .summary(buildSummaryFromProjects(page.getContent()))
                .data(ProjectReportMapper.toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 2. PROJECT STATUS GROUP REPORT
    // ======================================================
    @Override
    public List<ProjectStatusGroupReportDTO> getProjectStatusReport(
            ProjectReportFilterDTO filter, CustomUserDetails user) {

        List<Project> allProjects;

        if (isAdminOrHr(user)) {
            allProjects = projectReportRepository.findAllForStatusGrouping(
                    safeFrom(filter.getFromDate()), safeTo(filter.getToDate()));
        } else {
            // EMPLOYEE/TRAINEE/INTERN — fetch all assigned projects (no pagination)
            allProjects = findAssignedProjectList(user, filter);
        }

        Map<ProjectStatus, List<Project>> grouped = allProjects.stream()
                .collect(Collectors.groupingBy(p ->
                        p.getStatus() != null ? p.getStatus() : ProjectStatus.NOT_STARTED));

        return Arrays.stream(ProjectStatus.values())
                .map(status -> {
                    List<Project> group = grouped.getOrDefault(status, List.of());
                    List<ProjectMasterReportDTO> dtos = group.stream()
                            .map(p -> {
                                List<ProjectAssignment> assignments = assignmentRepository
                                        .findByProjectId(p.getId(), Pageable.unpaged()).getContent();
                                return buildMasterDTO(p, assignments);
                            })
                            .toList();
                    return ProjectStatusGroupReportDTO.builder()
                            .status(status)
                            .count(group.size())
                            .projects(dtos)
                            .build();
                })
                .toList();
    }

    // ======================================================
    // 3. PROJECT TIMELINE REPORT
    // ======================================================
    @Override
    public ProjectReportResponseDTO<ProjectTimelineReportDTO> getProjectTimelineReport(
            ProjectReportFilterDTO filter, Pageable pageable, CustomUserDetails user) {

        Page<Project> page = isAdminOrHr(user)
                ? projectReportRepository.findProjectsWithFilters(
                filter.getStatus(),
                safeFrom(filter.getFromDate()),
                safeTo(filter.getToDate()),
                safeId(filter.getProjectId()),
                pageable)
                : findAssignedProjectPage(user, filter, pageable);

        List<ProjectTimelineReportDTO> content = page.getContent().stream()
                .map(ProjectReportMapper::toTimelineDTO)
                .toList();

        return ProjectReportResponseDTO.<ProjectTimelineReportDTO>builder()
                .summary(buildSummaryFromProjects(page.getContent()))
                .data(ProjectReportMapper.toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 4. RESOURCE ALLOCATION REPORT (ADMIN only — enforced at controller)
    // ======================================================
    @Override
    public ProjectReportResponseDTO<ResourceAllocationReportDTO> getResourceAllocationReport(
            ProjectReportFilterDTO filter, Pageable pageable) {

        Page<Project> page = projectReportRepository.findProjectsWithFilters(
                filter.getStatus(),
                safeFrom(filter.getFromDate()),
                safeTo(filter.getToDate()),
                safeId(filter.getProjectId()),
                pageable);

        List<ResourceAllocationReportDTO> content = page.getContent().stream()
                .map(p -> buildAllocationDTO(p, filter))
                .toList();

        long totalAssignees = content.stream().mapToLong(r -> r.getTotalAssignees()).sum();

        ProjectReportSummaryDTO summary = buildSummaryFromProjects(page.getContent())
                .toBuilder()
                .totalAssignees(totalAssignees)
                .build();

        return ProjectReportResponseDTO.<ResourceAllocationReportDTO>builder()
                .summary(summary)
                .data(ProjectReportMapper.toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 5. PROJECT-WISE EMPLOYEE REPORT (ADMIN/HR)
    // ======================================================
    @Override
    public ResourceAllocationReportDTO getProjectWiseEmployeeReport(
            Long projectId, ProjectReportFilterDTO filter) {

        Project project = projectReportRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + projectId));

        return buildAllocationDTO(project, filter);
    }

    // ======================================================
    // 6. EMPLOYEE-WISE PROJECT REPORT
    // ======================================================
    @Override
    public ProjectReportResponseDTO<EmployeeProjectReportDTO> getEmployeeWiseProjectReport(
            ProjectReportFilterDTO filter, Pageable pageable, CustomUserDetails user) {

        // EMPLOYEE/TRAINEE/INTERN can only see their own projects
        if (!isAdminOrHr(user)) {
            filter.setPersonalInformationId(user.getUserId());
        }

        Long personalId = filter.getPersonalInformationId();
        if (personalId == null) {
            throw new InvalidRequestException("personalInformationId is required for this report");
        }

        PersonalInformation person = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + personalId));

        Page<ProjectAssignment> assignmentPage = resolveAssignmentPage(person, pageable);

        // Apply optional status filter after fetch (assignments don't carry filter)
        List<EmployeeProjectReportDTO> content = assignmentPage.getContent().stream()
                .filter(pa -> filter.getStatus() == null
                        || pa.getProject().getStatus() == filter.getStatus())
                .filter(pa -> filter.getFromDate() == null
                        || (pa.getProject().getStartDate() != null
                        && !pa.getProject().getStartDate().isBefore(filter.getFromDate())))
                .filter(pa -> filter.getToDate() == null
                        || (pa.getProject().getEndDate() != null
                        && !pa.getProject().getEndDate().isAfter(filter.getToDate())))
                .map(ProjectReportMapper::toEmployeeProjectDTO)
                .toList();

        List<Project> projects = assignmentPage.getContent().stream()
                .map(ProjectAssignment::getProject)
                .toList();

        return ProjectReportResponseDTO.<EmployeeProjectReportDTO>builder()
                .summary(buildSummaryFromProjects(projects))
                .data(ProjectReportMapper.toPageResponse(content, assignmentPage))
                .build();
    }

    // ======================================================
    // 7. PROJECT EFFORT REPORT
    // ======================================================
    @Override
    public ProjectReportResponseDTO<ProjectEffortReportDTO> getProjectEffortReport(
            ProjectReportFilterDTO filter, Pageable pageable, CustomUserDetails user) {

        if (!isAdminOrHr(user)) {
            filter.setPersonalInformationId(user.getUserId());
        }

        Page<Project> page = (filter.getPersonalInformationId() != null)
                ? findAssignedProjectPageById(filter.getPersonalInformationId(), filter, pageable)
                : projectReportRepository.findProjectsWithFilters(
                filter.getStatus(),
                safeFrom(filter.getFromDate()),
                safeTo(filter.getToDate()),
                safeId(filter.getProjectId()),
                pageable);

        List<ProjectEffortReportDTO> content = page.getContent().stream()
                .map(p -> {
                    long assigneeCount = assignmentRepository
                            .findByProjectId(p.getId(), Pageable.unpaged())
                            .getTotalElements();

                    String name = null, code = null;
                    AssigneeType type = null;
                    Double totalHours = null;
                    Long pid = filter.getPersonalInformationId();

                    if (pid != null) {
                        PersonalInformation pi = personalRepository.findById(pid).orElse(null);
                        if (pi != null) {
                            name = ProjectReportMapper.fullName(pi);
                            code = codeResolver.getCode(pid);
                            type = resolveAssigneeType(pi.getEmploymentType());
                            Long minutes = timesheetRepository.sumMinutesByPersonAndProject(pid, p.getId());
                            totalHours = minutes != null ? minutes / 60.0 : 0.0;
                        }
                    } else {
                        Long minutes = timesheetRepository.sumMinutesByProject(p.getId());
                        totalHours = minutes != null ? minutes / 60.0 : 0.0;
                    }

                    return ProjectReportMapper.toEffortDTO(
                            p, assigneeCount, pid, name, code, type, totalHours);
                })
                .toList();

        return ProjectReportResponseDTO.<ProjectEffortReportDTO>builder()
                .summary(buildSummaryFromProjects(page.getContent()))
                .data(ProjectReportMapper.toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 8. PROJECT COST REPORT
    // ======================================================
    @Override
    public ProjectReportResponseDTO<ProjectCostReportDTO> getProjectCostReport(
            ProjectReportFilterDTO filter, Pageable pageable) {

        Page<Project> page = projectReportRepository.findProjectsWithFilters(
                filter.getStatus(),
                safeFrom(filter.getFromDate()),
                safeTo(filter.getToDate()),
                safeId(filter.getProjectId()),
                pageable);

        List<ProjectCostReportDTO> content = page.getContent().stream()
                .map(ProjectReportMapper::toCostDTO)
                .toList();

        return ProjectReportResponseDTO.<ProjectCostReportDTO>builder()
                .summary(buildSummaryFromProjects(page.getContent()))
                .data(ProjectReportMapper.toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // PRIVATE HELPERS
    // ======================================================

    private boolean isAdminOrHr(CustomUserDetails user) {
        String role = user.getRole();
        return "ADMIN".equals(role) || "HR".equals(role);
    }

    /** Build ResourceAllocationReportDTO for a project, applying optional type/dept filters */
    private ResourceAllocationReportDTO buildAllocationDTO(Project p,
                                                           ProjectReportFilterDTO filter) {
        List<ProjectAssignment> allAssignments = assignmentRepository
                .findByProjectId(p.getId(), Pageable.unpaged()).getContent();

        List<ProjectAssigneeDTO> assigneeDTOs = allAssignments.stream()
                .filter(pa -> filter.getAssigneeType() == null
                        || pa.getAssigneeType() == filter.getAssigneeType())
                .filter(pa -> {
                    if (filter.getDepartmentId() == null) return true;
                    PersonalInformation pi = resolvePi(pa);
                    if (pi == null) return false;
                    WorkProfile wp = pi.getWorkProfile();
                    return wp != null && wp.getDepartment() != null
                            && filter.getDepartmentId().equals(wp.getDepartment().getId());
                })
                .map(ProjectReportMapper::toAssigneeDTO)
                .toList();

        long empCnt     = allAssignments.stream().filter(a -> a.getAssigneeType() == AssigneeType.EMPLOYEE).count();
        long traineeCnt = allAssignments.stream().filter(a -> a.getAssigneeType() == AssigneeType.TRAINEE).count();
        long internCnt  = allAssignments.stream().filter(a -> a.getAssigneeType() == AssigneeType.INTERN).count();

        return ResourceAllocationReportDTO.builder()
                .projectId(p.getId())
                .projectName(p.getProjectName())
                .projectCode(p.getProjectCode())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .projectStatus(p.getStatus())
                .totalAssignees(assigneeDTOs.size())
                .employeeCount((int) empCnt)
                .traineeCount((int) traineeCnt)
                .internCount((int) internCnt)
                .assignees(assigneeDTOs)
                .build();
    }

    private ProjectMasterReportDTO buildMasterDTO(Project p,
                                                  List<ProjectAssignment> assignments) {
        long emp     = assignments.stream().filter(a -> a.getAssigneeType() == AssigneeType.EMPLOYEE).count();
        long trainee = assignments.stream().filter(a -> a.getAssigneeType() == AssigneeType.TRAINEE).count();
        long intern  = assignments.stream().filter(a -> a.getAssigneeType() == AssigneeType.INTERN).count();
        return ProjectReportMapper.toMasterDTO(p, assignments.size(), emp, trainee, intern);
    }

    private Page<Project> findAssignedProjectPageById(Long personalId,
                                                      ProjectReportFilterDTO filter,
                                                      Pageable pageable) {
        PersonalInformation pi = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + personalId));
        return resolveProjectPage(pi.getEmploymentType(), personalId, filter, pageable);
    }

    private Page<Project> findAssignedProjectPage(CustomUserDetails user,
                                                  ProjectReportFilterDTO filter,
                                                  Pageable pageable) {
        Long personalId = user.getUserId();
        PersonalInformation pi = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        return resolveProjectPage(pi.getEmploymentType(), personalId, filter, pageable);
    }

    private Page<Project> resolveProjectPage(EmploymentType empType, Long personalId,
                                             ProjectReportFilterDTO filter, Pageable pageable) {
        return switch (empType) {
            case EMPLOYEE -> projectReportRepository.findProjectsByEmployeePersonalId(
                    personalId, filter.getStatus(),
                    safeFrom(filter.getFromDate()), safeTo(filter.getToDate()), pageable);
            case TRAINEE  -> projectReportRepository.findProjectsByTraineePersonalId(
                    personalId, filter.getStatus(),
                    safeFrom(filter.getFromDate()), safeTo(filter.getToDate()), pageable);
            case INTERN   -> projectReportRepository.findProjectsByInternPersonalId(
                    personalId, filter.getStatus(),
                    safeFrom(filter.getFromDate()), safeTo(filter.getToDate()), pageable);
            default -> Page.empty(pageable);
        };
    }

    private List<Project> findAssignedProjectList(CustomUserDetails user,
                                                  ProjectReportFilterDTO filter) {
        Long personalId = user.getUserId();
        PersonalInformation pi = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        // Fetch with large page — no Slice API here
        Page<Project> page = resolveProjectPage(pi.getEmploymentType(), personalId,
                filter, PageRequest.of(0, Integer.MAX_VALUE));
        return page.getContent();
    }

    private Page<ProjectAssignment> resolveAssignmentPage(PersonalInformation pi,
                                                          Pageable pageable) {
        return switch (pi.getEmploymentType()) {
            case EMPLOYEE -> {
                Employee emp = employeeRepository.findByPersonalInformationId(pi.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Employee profile not found for person id: " + pi.getId()));
                yield assignmentRepository.findByEmployeeId(emp.getId(), pageable);
            }
            case TRAINEE -> {
                Trainee tr = traineeRepository.findByPersonalInformationId(pi.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Trainee profile not found for person id: " + pi.getId()));
                yield assignmentRepository.findByTraineeId(tr.getId(), pageable);
            }
            case INTERN -> {
                Intern intern = internRepository.findByPersonalInformationId(pi.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Intern profile not found for person id: " + pi.getId()));
                yield assignmentRepository.findByInternId(intern.getId(), pageable);
            }
            default -> throw new InvalidRequestException(
                    "Unsupported employment type: " + pi.getEmploymentType());
        };
    }

    private PersonalInformation resolvePi(ProjectAssignment pa) {
        return switch (pa.getAssigneeType()) {
            case EMPLOYEE -> pa.getEmployee() != null
                    ? pa.getEmployee().getPersonalInformation() : null;
            case TRAINEE  -> pa.getTrainee() != null
                    ? pa.getTrainee().getPersonalInformation() : null;
            case INTERN   -> pa.getIntern() != null
                    ? pa.getIntern().getPersonalInformation() : null;
        };
    }

    private AssigneeType resolveAssigneeType(EmploymentType empType) {
        return switch (empType) {
            case EMPLOYEE -> AssigneeType.EMPLOYEE;
            case TRAINEE  -> AssigneeType.TRAINEE;
            case INTERN   -> AssigneeType.INTERN;
            default       -> null;
        };
    }

    private ProjectReportSummaryDTO buildSummaryFromProjects(List<Project> projects) {
        return ProjectReportSummaryDTO.builder()
                .totalProjects(projects.size())
                .notStarted(projects.stream()
                        .filter(p -> p.getStatus() == ProjectStatus.NOT_STARTED).count())
                .inProgress(projects.stream()
                        .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS).count())
                .completed(projects.stream()
                        .filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count())
                .onHold(projects.stream()
                        .filter(p -> p.getStatus() == ProjectStatus.ON_HOLD).count())
                .cancelled(projects.stream()
                        .filter(p -> p.getStatus() == ProjectStatus.CANCELLED).count())
                .delayed(projects.stream()
                        .filter(ProjectReportMapper::isDelayed).count())
                .build();
    }

    // ── Safe defaults ────────────────────────────────────────────────────────
    private Long safeId(Long id) {
        return id != null ? id : 0L;
    }

    private LocalDate safeFrom(LocalDate d) {
        return d != null ? d : LocalDate.of(2000, 1, 1);
    }

    private LocalDate safeTo(LocalDate d) {
        return d != null ? d : LocalDate.of(2100, 12, 31);
    }
}