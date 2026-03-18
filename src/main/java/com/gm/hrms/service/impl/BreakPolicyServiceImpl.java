package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.entity.BreakPolicy;
import com.gm.hrms.enums.BreakCategory;
import com.gm.hrms.mapper.BreakPolicyMapper;
import com.gm.hrms.repository.BreakPolicyRepository;
import com.gm.hrms.service.BreakPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BreakPolicyServiceImpl implements BreakPolicyService {

    private final BreakPolicyRepository repository;

    @Override
    public BreakPolicyResponseDTO create(BreakPolicyRequestDTO dto) {

        if(repository.existsByBreakName(dto.getBreakName())){
            throw new RuntimeException("Break policy already exists");
        }

        BreakPolicy entity = BreakPolicyMapper.toEntity(dto);

        entity.setBreakCategory(
                dto.getBreakCategory()
        );

        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());

        repository.save(entity);

        return BreakPolicyMapper.toResponse(entity);
    }

    @Override
    public BreakPolicyResponseDTO update(Long id, BreakPolicyRequestDTO dto) {

        BreakPolicy entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Break policy not found"));

        BreakPolicyMapper.patchEntity(entity, dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        return BreakPolicyMapper.toResponse(entity);
    }

    @Override
    public BreakPolicyResponseDTO getById(Long id) {

        BreakPolicy entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Break policy not found"));

        return BreakPolicyMapper.toResponse(entity);
    }

    @Override
    public List<BreakPolicyResponseDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(BreakPolicyMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {

        BreakPolicy entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Break policy not found"));

        entity.setIsActive(false);

        repository.save(entity);
    }
}