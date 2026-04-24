package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.BranchRequestDTO;
import com.gm.hrms.dto.request.BranchUpdateDTO;
import com.gm.hrms.dto.response.BranchMoveDTO;
import com.gm.hrms.dto.response.BranchResponseDTO;
import com.gm.hrms.dto.response.BranchTreeNodeDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Address;
import com.gm.hrms.entity.Branch;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AddressMapper;
import com.gm.hrms.mapper.BranchMapper;
import com.gm.hrms.repository.AddressRepository;
import com.gm.hrms.repository.BranchRepository;
import com.gm.hrms.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        Branch parent = null;
        if (dto.getParentId() != null) {
            parent = branchRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent branch not found"));
        }

        Address address = AddressMapper.toEntity(dto.getAddress());
        addressRepository.save(address);

        Branch branch = BranchMapper.toEntity(dto, address, parent); // mapper updated below
        branchRepository.save(branch);

        return BranchMapper.toResponse(branch);
    }

    @Override
    public BranchResponseDTO update(Long id, BranchUpdateDTO dto) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(id)) {
                throw new InvalidRequestException("A branch cannot be its own parent");
            }
            Branch parent = branchRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent branch not found"));
            branch.setParent(parent);
        }

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
    public PageResponseDTO<BranchResponseDTO> getAll(Pageable pageable) {

        Page<Branch> page = branchRepository.findAll(pageable);

        List<BranchResponseDTO> content = page.getContent()
                .stream()
                .map(BranchMapper::toResponse)
                .toList();

        return PageResponseDTO.<BranchResponseDTO>builder()
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

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        branch.setActive(false);
    }

    @Override
    public BranchResponseDTO move(Long id, BranchMoveDTO dto) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (dto.getNewParentId() == null) {
            branch.setParent(null); // promote to root
        } else {
            if (dto.getNewParentId().equals(id)) {
                throw new InvalidRequestException("A branch cannot be its own parent");
            }
            Branch newParent = branchRepository.findById(dto.getNewParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target parent branch not found"));

            // Prevent circular hierarchy
            if (isDescendant(newParent, id)) {
                throw new InvalidRequestException("Cannot move branch into one of its own descendants");
            }

            branch.setParent(newParent);
        }

        branchRepository.save(branch);
        return BranchMapper.toResponse(branch);
    }

    // Data from the user axis to be continued with accepted data concept
    @Override
    public List<BranchTreeNodeDTO> getTree() {
        List<Branch> roots = branchRepository.findByParentIsNull();
        return roots.stream()
                .map(this::buildTreeNode)
                .toList();
    }

    private BranchTreeNodeDTO buildTreeNode(Branch branch) {
        return BranchTreeNodeDTO.builder()
                .id(branch.getId())
                .branchName(branch.getBranchName())
                .branchCode(branch.getBranchCode())
                .active(branch.getActive())
                .parentId(branch.getParent() != null ? branch.getParent().getId() : null)
                .children(
                        branch.getChildren().stream()
                                .map(this::buildTreeNode)
                                .toList()
                )
                .build();
    }

    private boolean isDescendant(Branch candidate, Long ancestorId) {
        if (candidate.getParent() == null) return false;
        if (candidate.getParent().getId().equals(ancestorId)) return true;
        return isDescendant(candidate.getParent(), ancestorId);
    }
}