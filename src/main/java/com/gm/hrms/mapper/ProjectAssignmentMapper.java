package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.ProjectAssignmentResponseDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.ProjectAssignment;
import com.gm.hrms.entity.Trainee;

public class ProjectAssignmentMapper {

    public static ProjectAssignmentResponseDTO toResponse(ProjectAssignment pa) {

        ProjectAssignmentResponseDTO.ProjectAssignmentResponseDTOBuilder builder =
                ProjectAssignmentResponseDTO.builder()
                        .id(pa.getId())
                        .roleInProject(pa.getRoleInProject())
                        .assigneeType(pa.getAssigneeType())
                        .projectId(pa.getProject().getId())
                        .projectName(pa.getProject().getProjectName())
                        .projectCode(pa.getProject().getProjectCode());

        switch (pa.getAssigneeType()) {
            case EMPLOYEE -> {
                Employee emp = pa.getEmployee();
                PersonalInformation pi = emp.getPersonalInformation();
                builder.assigneeId(emp.getId())
                        .assigneeCode(emp.getEmployeeCode())
                        .assigneeName(pi.getFirstName() + " " + pi.getLastName());
            }
            case TRAINEE -> {
                Trainee tr = pa.getTrainee();
                PersonalInformation pi = tr.getPersonalInformation();
                builder.assigneeId(tr.getId())
                        .assigneeCode(tr.getTraineeCode())
                        .assigneeName(pi.getFirstName() + " " + pi.getLastName());
            }
            case INTERN -> {
                Intern intern = pa.getIntern();
                PersonalInformation pi = intern.getPersonalInformation();
                builder.assigneeId(intern.getId())
                        .assigneeCode(intern.getInternCode())
                        .assigneeName(pi.getFirstName() + " " + pi.getLastName());
            }
        }

        return builder.build();
    }
}