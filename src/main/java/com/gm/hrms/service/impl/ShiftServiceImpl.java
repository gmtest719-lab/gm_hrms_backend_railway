package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.ShiftRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ShiftResponseDTO;
import com.gm.hrms.entity.Shift;
import com.gm.hrms.entity.ShiftBreakMapping;
import com.gm.hrms.entity.ShiftDayConfig;
import com.gm.hrms.entity.ShiftTiming;
import com.gm.hrms.enums.ShiftType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.ShiftMapper;
import com.gm.hrms.repository.ShiftBreakMappingRepository;
import com.gm.hrms.repository.ShiftDayConfigRepository;
import com.gm.hrms.repository.ShiftRepository;
import com.gm.hrms.repository.ShiftTimingRepository;
import com.gm.hrms.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftTimingRepository shiftTimingRepository;
    private final ShiftDayConfigRepository shiftDayConfigRepository;
    private final ShiftBreakMappingRepository shiftBreakMappingRepository;

    @Override
    public ShiftResponseDTO create(ShiftRequestDTO dto) {

        // ---------- VALIDATION ----------
        if(dto.getShiftType() == ShiftType.NORMAL && dto.getNormalTiming() == null){
            throw new InvalidRequestException("Normal shift must contain normalTiming");
        }

        if(dto.getShiftType() == ShiftType.CUSTOM && (dto.getDayConfigs() == null || dto.getDayConfigs().isEmpty())){
            throw new InvalidRequestException("Custom shift must contain dayConfigs");
        }

        Shift shift = ShiftMapper.toEntity(dto);

        shift.setIsActive(true);
        shift.setCreatedAt(LocalDateTime.now());

        shiftRepository.save(shift);

        // ---------- NORMAL SHIFT ----------
        if(dto.getShiftType() == ShiftType.NORMAL){

            ShiftTiming timing =
                    ShiftMapper.toTiming(dto.getNormalTiming(), shift);

            shiftTimingRepository.save(timing);
        }

        // ---------- CUSTOM SHIFT ----------
        if(dto.getShiftType() == ShiftType.CUSTOM){

            List<ShiftDayConfig> configs =
                    ShiftMapper.toDayConfigs(dto.getDayConfigs(), shift);

            shiftDayConfigRepository.saveAll(configs);
        }

        // ---------- BREAK MAPPING ----------
        if(dto.getBreakIds() != null && !dto.getBreakIds().isEmpty()){

            List<ShiftBreakMapping> mappings =
                    ShiftMapper.toBreakMappings(dto.getBreakIds(), shift);

            shiftBreakMappingRepository.saveAll(mappings);
        }

        // reload shift with relationships
        Shift savedShift = shiftRepository.findById(shift.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        return ShiftMapper.toResponse(savedShift);
    }

    @Override
    public ShiftResponseDTO getById(Long id) {

        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        return ShiftMapper.toResponse(shift);
    }


    @Override
    public PageResponseDTO<ShiftResponseDTO> getAll(Pageable pageable) {

        Page<Shift> page = shiftRepository.findAll(pageable);

        List<ShiftResponseDTO> content = page.getContent()
                .stream()
                .map(ShiftMapper::toResponse)
                .toList();

        return PageResponseDTO.<ShiftResponseDTO>builder()
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
    public void delete(Long id) {

        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        shift.setIsActive(false);

        shiftRepository.save(shift);
    }
}