package com.gm.hrms.service;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.enums.ApplicableType;

import java.util.List;

public interface DocumentTypeService {

    DocumentTypeResponseDTO create(DocumentTypeRequestDTO dto);

    DocumentTypeResponseDTO update(Long id, DocumentTypeRequestDTO dto);

    void delete(Long id);

    List<DocumentTypeResponseDTO> getAll();

    DocumentTypeResponseDTO getById(Long id);

    List<DocumentTypeResponseDTO> getByApplicableType(ApplicableType type);
}