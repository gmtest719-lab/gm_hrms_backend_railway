package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employee_documents")
@Data
public class EmployeeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_type")
    private String documentType; // ID Proof, Certificate

    @Column(name = "file_path")
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
