package com.gm.hrms.entity;

import com.gm.hrms.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employee_employment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEmployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateOfJoining;

    private Integer yearOfExperience;

    private Double ctc;

    @ElementCollection
    @CollectionTable(
            name = "employee_previous_companies",
            joinColumns = @JoinColumn(name = "employee_employment_id")
    )
    @Column(name = "company_name", nullable = false)
    private List<String> previousCompanyNames;

    private Integer noticePeriod;

    @OneToOne
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;
}