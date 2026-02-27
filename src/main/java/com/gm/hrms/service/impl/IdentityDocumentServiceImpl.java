package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.IdentityDocumentRequestDTO;
import com.gm.hrms.dto.response.IdentityDocumentResponseDTO;
import com.gm.hrms.entity.DocumentType;
import com.gm.hrms.entity.IdentityDocument;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.DocumentTypeRepository;
import com.gm.hrms.repository.IdentityDocumentRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.service.IdentityDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IdentityDocumentServiceImpl implements IdentityDocumentService {

    private final IdentityDocumentRepository repository;
    private final DocumentTypeRepository documentTypeRepository;
    private final PersonalInformationRepository personalInformationRepository;

    @Override
    public void uploadDocuments(
            PersonalInformation person,
            List<MultipartFile> files,
            List<IdentityDocumentRequestDTO> metadata) {

        for (int i = 0; i < metadata.size(); i++) {

            IdentityDocumentRequestDTO dto = metadata.get(i);

            DocumentType type = documentTypeRepository.findById(dto.getDocumentTypeId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Document type not found"));

            MultipartFile file =
                    (files != null && files.size() > i)
                            ? files.get(i)
                            : null;

            // Mandatory validation
            if (Boolean.TRUE.equals(type.getMandatory())
                    && (file == null || file.isEmpty())
                    && (dto.getReasonIfMissing() == null
                    || dto.getReasonIfMissing().isBlank())) {

                throw new RuntimeException(
                        type.getName() + " is mandatory. Upload file or provide reason."
                );
            }

            String filePath = null;

            if (file != null && !file.isEmpty()) {
                filePath = "uploads/" + file.getOriginalFilename();
            }

            IdentityDocument document = IdentityDocument.builder()
                    .personalInformation(person)
                    .documentType(type)
                    .filePath(filePath)
                    .reasonIfMissing(dto.getReasonIfMissing())
                    .verified(false)
                    .build();

            repository.save(document);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityDocumentResponseDTO> getByPersonalInformation(Long personId) {

        PersonalInformation person = personalInformationRepository.findById(personId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Personal information not found"));

        return repository.findByPersonalInformation(person)
                .stream()
                .map(doc -> IdentityDocumentResponseDTO.builder()
                        .id(doc.getId())
                        .documentName(doc.getDocumentType().getName())
                        .mandatory(doc.getDocumentType().getMandatory())
                        .filePath(doc.getFilePath())
                        .reasonIfMissing(doc.getReasonIfMissing())
                        .verified(doc.getVerified())
                        .verifiedAt(doc.getVerifiedAt())
                        .build())
                .toList();
    }

    @Override
    public void verifyDocument(Long documentId, Long verifiedBy) {

        IdentityDocument doc = repository.findById(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));

        doc.setVerified(true);
        doc.setVerifiedBy(verifiedBy);
        doc.setVerifiedAt(LocalDateTime.now());
    }
}