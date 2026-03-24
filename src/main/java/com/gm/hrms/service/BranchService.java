package com.gm.hrms.service;

import com.gm.hrms.dto.request.BranchRequestDTO;
import com.gm.hrms.dto.request.BranchUpdateDTO;
import com.gm.hrms.dto.response.BranchResponseDTO;

import java.util.List;

public interface BranchService {

    BranchResponseDTO create(BranchRequestDTO dto);

    BranchResponseDTO update(Long id, BranchUpdateDTO dto);

    BranchResponseDTO getById(Long id);

    List<BranchResponseDTO> getAll();

    void delete(Long id);
}