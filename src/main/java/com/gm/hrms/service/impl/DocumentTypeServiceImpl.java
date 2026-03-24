package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.DocumentType;
import com.gm.hrms.enums.ApplicableType;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.DocumentTypeMapper;
import com.gm.hrms.repository.DocumentTypeRepository;
import com.gm.hrms.service.DocumentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentTypeRepository repository;

    @Override
    public DocumentTypeResponseDTO create(DocumentTypeRequestDTO dto) {

        if(repository.existsByNameIgnoreCase(dto.getName()))
            throw new DuplicateResourceException("Document name already exists");

        if(repository.existsByKeyIgnoreCase(dto.getKey()))
            throw new DuplicateResourceException("Document key already exists");

        DocumentType entity = DocumentTypeMapper.toEntity(dto);

        repository.save(entity);

        return DocumentTypeMapper.toResponse(entity);
    }

    @Override
    public DocumentTypeResponseDTO update(Long id, DocumentTypeRequestDTO dto) {

        DocumentType entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document type not found"));

        DocumentTypeMapper.updateEntity(entity, dto);

        repository.save(entity);

        return DocumentTypeMapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {

        DocumentType entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document type not found"));

        entity.setActive(false);
    }

    @Override
    public PageResponseDTO<DocumentTypeResponseDTO> getAll(Pageable pageable) {

        Page<DocumentType> page = repository.findByActiveTrue(pageable);

        List<DocumentTypeResponseDTO> content = page.getContent()
                .stream()
                .map(DocumentTypeMapper::toResponse)
                .toList();

        return PageResponseDTO.<DocumentTypeResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Override
    public DocumentTypeResponseDTO getById(Long id) {

        DocumentType entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document type not found"));

        return DocumentTypeMapper.toResponse(entity);
    }

    @Override
    public PageResponseDTO<DocumentTypeResponseDTO> getByApplicableType(
            ApplicableType type,
            Pageable pageable) {

        Page<DocumentType> page =
                repository.findByApplicableTypesContainingAndActiveTrue(type, pageable);

        List<DocumentTypeResponseDTO> content = page.getContent()
                .stream()
                .map(DocumentTypeMapper::toResponse)
                .toList();

        return PageResponseDTO.<DocumentTypeResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}