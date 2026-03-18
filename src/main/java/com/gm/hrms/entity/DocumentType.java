package com.gm.hrms.entity;

import com.gm.hrms.enums.ApplicableType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "document_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // UNIQUE KEY USED IN FILE UPLOAD
    @Column(nullable = false, unique = true)
    private String key;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "document_type_applicable")
    private Set<ApplicableType> applicableTypes;

    private Boolean mandatory;

    private Boolean active;
}