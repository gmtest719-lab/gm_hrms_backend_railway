package com.gm.hrms.repository;

import com.gm.hrms.entity.IdentityDocument;
import com.gm.hrms.entity.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdentityDocumentRepository
        extends JpaRepository<IdentityDocument, Long> {

    List<IdentityDocument> findByPersonalInformation(PersonalInformation personalInformation);

    boolean existsByPersonalInformationAndDocumentType_Id(
            PersonalInformation personalInformation,
            Long documentTypeId
    );
}