package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.BranchRequestDTO;
import com.gm.hrms.dto.request.BranchUpdateDTO;
import com.gm.hrms.dto.response.BranchResponseDTO;
import com.gm.hrms.entity.Address;
import com.gm.hrms.entity.Branch;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AddressMapper;
import com.gm.hrms.mapper.BranchMapper;
import com.gm.hrms.repository.AddressRepository;
import com.gm.hrms.repository.BranchRepository;
import com.gm.hrms.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final AddressRepository addressRepository;

    @Override
    public BranchResponseDTO create(BranchRequestDTO dto) {

        if (branchRepository.existsByBranchCode(dto.getBranchCode())) {
            throw new DuplicateResourceException("Branch code already exists");
        }

        Address address = AddressMapper.toEntity(dto.getAddress());

        addressRepository.save(address);

        Branch branch = BranchMapper.toEntity(dto, address);

        branchRepository.save(branch);

        return BranchMapper.toResponse(branch);
    }

    @Override
    public BranchResponseDTO update(Long id, BranchUpdateDTO dto) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        Address address = branch.getAddress();

        if (dto.getAddress() != null) {

            if (address == null) {
                address = AddressMapper.toEntity(dto.getAddress());
                addressRepository.save(address);
            } else {
                AddressMapper.patchEntity(address, dto.getAddress());
            }
        }

        BranchMapper.patchEntity(branch, dto, address);

        branchRepository.save(branch);

        return BranchMapper.toResponse(branch);
    }

    @Override
    public BranchResponseDTO getById(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        return BranchMapper.toResponse(branch);
    }

    @Override
    public List<BranchResponseDTO> getAll() {

        return branchRepository.findAll()
                .stream()
                .map(BranchMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        branch.setActive(false);
    }
}