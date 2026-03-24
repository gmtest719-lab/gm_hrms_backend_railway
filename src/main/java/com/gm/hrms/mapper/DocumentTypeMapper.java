package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.entity.DocumentType;

public class DocumentTypeMapper {

    private DocumentTypeMapper(){}

    public static DocumentType toEntity(DocumentTypeRequestDTO dto) {

        return DocumentType.builder()
                .name(dto.getName())
                .key(dto.getKey())
                .applicableTypes(dto.getApplicableTypes())
                .mandatory(dto.getMandatory() != null ? dto.getMandatory() : false)
                .active(true)
                .build();
    }

    public static void updateEntity(DocumentType entity,
                                    DocumentTypeRequestDTO dto) {

        if(dto.getName() != null)
            entity.setName(dto.getName());

        if(dto.getKey() != null)
            entity.setKey(dto.getKey());

        if(dto.getMandatory() != null)
            entity.setMandatory(dto.getMandatory());

        if(dto.getApplicableTypes() != null && !dto.getApplicableTypes().isEmpty())
            entity.setApplicableTypes(dto.getApplicableTypes());
    }

    public static DocumentTypeResponseDTO toResponse(DocumentType entity) {

        return DocumentTypeResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .key(entity.getKey())
                .applicableTypes(entity.getApplicableTypes())
                .mandatory(entity.getMandatory())
                .active(entity.getActive())
                .build();
    }
}