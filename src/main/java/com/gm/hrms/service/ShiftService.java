package com.gm.hrms.service;

import com.gm.hrms.dto.request.ShiftRequestDTO;
import com.gm.hrms.dto.response.ShiftResponseDTO;

import java.util.List;

public interface ShiftService {

    ShiftResponseDTO create(ShiftRequestDTO dto);

    ShiftResponseDTO getById(Long id);

    List<ShiftResponseDTO> getAll();

    void delete(Long id);
}