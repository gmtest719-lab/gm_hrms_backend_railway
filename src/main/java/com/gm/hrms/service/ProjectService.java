package com.gm.hrms.service;

import com.gm.hrms.dto.request.ProjectRequestDTO;
import com.gm.hrms.dto.response.ProjectResponseDTO;

import java.util.List;

public interface ProjectService {

    ProjectResponseDTO create(ProjectRequestDTO dto);

    ProjectResponseDTO update(Long id, ProjectRequestDTO dto);

    ProjectResponseDTO getById(Long id);

    List<ProjectResponseDTO> getAll();

    void delete(Long id);
}

