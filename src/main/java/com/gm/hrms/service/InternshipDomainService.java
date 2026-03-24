package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternshipDomainRequestDTO;
import com.gm.hrms.dto.response.InternshipDomainResponseDTO;

import java.util.List;

public interface InternshipDomainService {

    InternshipDomainResponseDTO create(InternshipDomainRequestDTO dto);

    InternshipDomainResponseDTO update(Long id,
                                       InternshipDomainRequestDTO dto);

    InternshipDomainResponseDTO getById(Long id);

    List<InternshipDomainResponseDTO> getAll();

    void delete(Long id); // soft delete
}