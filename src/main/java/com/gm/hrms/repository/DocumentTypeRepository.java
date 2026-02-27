package com.gm.hrms.repository;

import com.gm.hrms.entity.DocumentType;
import com.gm.hrms.enums.ApplicableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<DocumentType> findByActiveTrue();

    List<DocumentType> findByApplicableTypesContainingAndActiveTrue(ApplicableType type);
}