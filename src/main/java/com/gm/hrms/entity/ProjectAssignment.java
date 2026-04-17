package com.gm.hrms.entity;

import com.gm.hrms.enums.AssigneeType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "project_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"project_id", "employee_id"}),
                @UniqueConstraint(columnNames = {"project_id", "trainee_id"}),
                @UniqueConstraint(columnNames = {"project_id", "intern_id"})
        }
)
@Data
public class ProjectAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_in_project")
    private String roleInProject;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignee_type", nullable = false)
    private AssigneeType assigneeType;  // EMPLOYEE / TRAINEE / INTERN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intern_id")
    private Intern intern;
}