package com.gm.hrms.entity;

import com.gm.hrms.enums.ApplicableType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "document_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection(targetClass = ApplicableType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "document_type_applicable",
            joinColumns = @JoinColumn(name = "document_type_id")
    )
    @Column(name = "applicable_type")
    private Set<ApplicableType> applicableTypes;

    @Column(nullable = false)
    private Boolean mandatory = false;

    @Column(nullable = false)
    private Boolean active = true;
}