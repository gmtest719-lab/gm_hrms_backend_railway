package com.gm.hrms.service;

import com.gm.hrms.dto.request.ShiftRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ShiftResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShiftService {

    ShiftResponseDTO create(ShiftRequestDTO dto);

    ShiftResponseDTO getById(Long id);

    PageResponseDTO<ShiftResponseDTO> getAll(Pageable pageable);

    void delete(Long id);
}