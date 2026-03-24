package com.gm.hrms.service;

import com.gm.hrms.dto.request.HolidayRequestDTO;
import com.gm.hrms.dto.response.HolidayResponseDTO;

import java.util.List;

public interface HolidayService {

    HolidayResponseDTO create(HolidayRequestDTO dto);

    HolidayResponseDTO update(Long id, HolidayRequestDTO dto);

    HolidayResponseDTO getById(Long id);

    List<HolidayResponseDTO> getAll();

    void delete(Long id);
}