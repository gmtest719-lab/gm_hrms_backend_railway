package com.gm.hrms.repository;

import com.gm.hrms.entity.DocumentType;
import com.gm.hrms.enums.ApplicableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByKeyIgnoreCase(String key);

    Optional<DocumentType> findByKey(String key);

    List<DocumentType> findByActiveTrue();

    List<DocumentType> findByApplicableTypesContainingAndActiveTrue(ApplicableType type);
}