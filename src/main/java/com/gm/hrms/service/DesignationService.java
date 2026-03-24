package com.gm.hrms.service;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.dto.response.DesignationResponseDTO;

import java.util.List;

public interface DesignationService {

    DesignationResponseDTO create(DesignationRequestDTO dto);

    DesignationResponseDTO update(Long id, DesignationRequestDTO dto);

    DesignationResponseDTO getById(Long id);

    List<DesignationResponseDTO> getAll();

    void delete(Long id);
}
