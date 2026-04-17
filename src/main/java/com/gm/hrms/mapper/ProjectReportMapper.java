// com/gm/hrms/mapper/ProjectReportMapper.java
package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AssigneeType;
import com.gm.hrms.enums.ProjectStatus;
import com.gm.hrms.repository.TimesheetRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ProjectReportMapper {

    private final TimesheetRepository timesheetRepository;

    // PROJECT MASTER
    public static ProjectMasterReportDTO toMasterDTO(Project p,
                                                     long totalAssignees,
                                                     long empCount,
                                                     long traineeCount,
                                                     long internCount) {
        return ProjectMasterReportDTO.builder()
                .projectId(p.getId())
                .projectName(p.getProjectName())
                .projectCode(p.getProjectCode())
                .description(p.getDescription())
                .clientName(p.getClientName())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus())
                .totalAssignees(totalAssignees)
                .employeeCount(empCount)
                .traineeCount(traineeCount)
                .internCount(internCount)
                .delayed(isDelayed(p))
                .daysOverdue(daysOverdue(p))
                .build();
    }

    // ─────────────────── TIMELINE ────────────────────────────────────────────
    public static ProjectTimelineReportDTO toTimelineDTO(Project p) {
        LocalDate today = LocalDate.now();

        long durationDays = 0;
        long elapsedDays  = 0;
        int  progress     = 0;
        long remaining    = 0;

        if (p.getStartDate() != null && p.getEndDate() != null) {
            durationDays = Math.max(1, ChronoUnit.DAYS.between(p.getStartDate(), p.getEndDate()));
            elapsedDays  = Math.max(0, Math.min(
                    ChronoUnit.DAYS.between(p.getStartDate(), today), durationDays));
            remaining    = Math.max(0, ChronoUnit.DAYS.between(today, p.getEndDate()));
        }

        if (p.getStatus() == ProjectStatus.COMPLETED) {
            progress  = 100;
            remaining = 0;
        } else if (p.getStatus() == ProjectStatus.NOT_STARTED) {
            progress = 0;
        } else {
            progress = durationDays > 0 ? (int) ((elapsedDays * 100) / durationDays) : 0;
        }

        return ProjectTimelineReportDTO.builder()
                .projectId(p.getId())
                .projectName(p.getProjectName())
                .projectCode(p.getProjectCode())
                .clientName(p.getClientName())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus())
                .durationDays(durationDays)
                .elapsedDays(elapsedDays)
                .progressPercent(progress)
                .delayed(isDelayed(p))
                .daysOverdue(daysOverdue(p))
                .daysRemaining(remaining)
                .build();
    }

    // ─────────────────── EMPLOYEE-WISE PROJECT ───────────────────────────────
    public static EmployeeProjectReportDTO toEmployeeProjectDTO(ProjectAssignment pa) {
        Project p = pa.getProject();
        return EmployeeProjectReportDTO.builder()
                .assignmentId(pa.getId())
                .projectId(p.getId())
                .projectName(p.getProjectName())
                .projectCode(p.getProjectCode())
                .description(p.getDescription())
                .clientName(p.getClientName())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .projectStatus(p.getStatus())
                .roleInProject(pa.getRoleInProject())
                .assigneeType(pa.getAssigneeType())
                .delayed(isDelayed(p))
                .daysOverdue(daysOverdue(p))
                .build();
    }

    // ─────────────────── EFFORT ──────────────────────────────────────────────
    public static ProjectEffortReportDTO toEffortDTO(Project p,
                                                     long totalAssignees,
                                                     Long assigneePersonalId,
                                                     String assigneeName,
                                                     String assigneeCode,
                                                     AssigneeType assigneeType,
                                                     Double totalHours) {   // ← added
        return ProjectEffortReportDTO.builder()
                .projectId(p.getId())
                .projectName(p.getProjectName())
                .projectCode(p.getProjectCode())
                .projectStatus(p.getStatus())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .totalAssignees(totalAssignees)
                .assigneePersonalInformationId(assigneePersonalId)
                .assigneeName(assigneeName)
                .assigneeCode(assigneeCode)
                .assigneeType(assigneeType)
                .totalHours(totalHours)           // ← passed in, not fetched here
                .timesheetNote(totalHours == null
                        ? "No timesheet data found for this project/assignee"
                        : null)
                .build();
    }

    // ─────────────────── COST ────────────────────────────────────────────────
    public static ProjectCostReportDTO toCostDTO(Project p) {
        Double budget = p.getBudgetAmount();
        Double actual = p.getActualCost();
        Double variance        = (budget != null && actual != null) ? budget - actual : null;
        Double variancePct     = (variance != null && budget != null && budget != 0)
                ? (variance / budget) * 100 : null;

        return ProjectCostReportDTO.builder()
                .projectId(p.getId())
                .projectName(p.getProjectName())
                .projectCode(p.getProjectCode())
                .status(p.getStatus())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .clientName(p.getClientName())
                .budgetAmount(budget)
                .actualCost(actual)
                .variance(variance)
                .variancePercent(variancePct)
                .costNote("Add 'budgetAmount' and 'actualCost' fields to Project entity to enable cost reporting")
                .build();
    }

    // ─────────────────── ASSIGNEE ────────────────────────────────────────────
    public static ProjectAssigneeDTO toAssigneeDTO(ProjectAssignment pa) {
        ProjectAssigneeDTO.ProjectAssigneeDTOBuilder b = ProjectAssigneeDTO.builder()
                .assignmentId(pa.getId())
                .roleInProject(pa.getRoleInProject())
                .assigneeType(pa.getAssigneeType());

        switch (pa.getAssigneeType()) {
            case EMPLOYEE -> {
                Employee emp = pa.getEmployee();
                if (emp != null) {
                    enrichFromPerson(b, emp.getId(),
                            emp.getPersonalInformation(), emp.getEmployeeCode());
                }
            }
            case TRAINEE -> {
                Trainee tr = pa.getTrainee();
                if (tr != null) {
                    enrichFromPerson(b, tr.getId(),
                            tr.getPersonalInformation(), tr.getTraineeCode());
                }
            }
            case INTERN -> {
                Intern intern = pa.getIntern();
                if (intern != null) {
                    enrichFromPerson(b, intern.getId(),
                            intern.getPersonalInformation(), intern.getInternCode());
                }
            }
        }

        return b.build();
    }

    // ─────────────────── HELPERS ─────────────────────────────────────────────
    private static void enrichFromPerson(ProjectAssigneeDTO.ProjectAssigneeDTOBuilder b,
                                         Long entityId,
                                         PersonalInformation pi,
                                         String code) {
        if (pi == null) return;
        WorkProfile wp = pi.getWorkProfile();
        b.assigneeId(entityId)
                .personalInformationId(pi.getId())
                .assigneeCode(code)
                .assigneeName(fullName(pi))
                .department(wp != null && wp.getDepartment() != null
                        ? wp.getDepartment().getName() : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null);
    }

    public static boolean isDelayed(Project p) {
        if (p.getEndDate() == null || p.getStatus() == null) return false;
        if (p.getStatus() == ProjectStatus.COMPLETED
                || p.getStatus() == ProjectStatus.CANCELLED) return false;
        return p.getEndDate().isBefore(LocalDate.now());
    }

    public static long daysOverdue(Project p) {
        if (!isDelayed(p)) return 0;
        return ChronoUnit.DAYS.between(p.getEndDate(), LocalDate.now());
    }

    public static String fullName(PersonalInformation pi) {
        if (pi == null) return "";
        String mid = (pi.getMiddleName() != null && !pi.getMiddleName().isBlank())
                ? pi.getMiddleName() + " " : "";
        return pi.getFirstName() + " " + mid + pi.getLastName();
    }

    public static <T> PageResponseDTO<T> toPageResponse(List<T> content,
                                                        org.springframework.data.domain.Page<?> page) {
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
}