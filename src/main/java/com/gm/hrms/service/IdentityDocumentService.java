package com.gm.hrms.service;

import com.gm.hrms.dto.request.IdentityDocumentRequestDTO;
import com.gm.hrms.dto.response.IdentityDocumentResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IdentityDocumentService {

    void uploadDocuments(
            PersonalInformation personalInformation,
            List<MultipartFile> files,
            List<IdentityDocumentRequestDTO> metadata
    );

    List<IdentityDocumentResponseDTO> getByPersonalInformation(Long personalInformationId);

    void verifyDocument(Long documentId, Long verifiedBy);
}