package com.gm.hrms.repository;

import com.gm.hrms.entity.DocumentType;
import com.gm.hrms.enums.ApplicableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByKeyIgnoreCase(String key);

    Optional<DocumentType> findByKey(String key);

    Page<DocumentType> findByActiveTrue(Pageable pageable);

    List<DocumentType> findByApplicableTypesContainingAndActiveTrue(ApplicableType type);

    Page<DocumentType> findByApplicableTypesContainingAndActiveTrue(
            ApplicableType type,
            Pageable pageable
    );
}