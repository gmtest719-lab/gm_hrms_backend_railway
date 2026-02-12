package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeaveTypeResponseDTO;
import com.gm.hrms.entity.LeaveType;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.LeaveTypeRepository;
import com.gm.hrms.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository repo;

    @Override
    public LeaveTypeResponseDTO create(LeaveTypeRequestDTO dto) {

        if(repo.existsByName(dto.getName())){
            throw new DuplicateResourceException("Leave type already exists");
        }

        LeaveType type = new LeaveType();
        type.setName(dto.getName());

        return map(repo.save(type));
    }

    @Override
    public List<LeaveTypeResponseDTO> getAll() {

        return repo.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void delete(Long id) {

        LeaveType type = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        repo.delete(type);
    }

    private LeaveTypeResponseDTO map(LeaveType t){
        return LeaveTypeResponseDTO.builder()
                .id(t.getId())
                .name(t.getName())
                .build();
    }
}
