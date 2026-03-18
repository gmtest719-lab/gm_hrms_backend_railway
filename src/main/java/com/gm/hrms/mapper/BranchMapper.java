package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.BranchRequestDTO;
import com.gm.hrms.dto.request.BranchUpdateDTO;
import com.gm.hrms.dto.response.BranchResponseDTO;
import com.gm.hrms.entity.Address;
import com.gm.hrms.entity.Branch;

public class BranchMapper {

    private BranchMapper() {}

    public static Branch toEntity(BranchRequestDTO dto, Address address) {

        Branch branch = new Branch();

        branch.setBranchName(dto.getBranchName());
        branch.setBranchCode(dto.getBranchCode());
        branch.setAddress(address);
        branch.setActive(true);

        return branch;
    }

    public static void patchEntity(Branch branch,
                                   BranchUpdateDTO dto,
                                   Address address) {

        if (dto.getBranchName() != null)
            branch.setBranchName(dto.getBranchName());

        if (dto.getBranchCode() != null)
            branch.setBranchCode(dto.getBranchCode());

        if (dto.getActive() != null)
            branch.setActive(dto.getActive());

        if (address != null)
            branch.setAddress(address);
    }

    public static BranchResponseDTO toResponse(Branch branch) {

        return BranchResponseDTO.builder()
                .id(branch.getId())
                .branchName(branch.getBranchName())
                .branchCode(branch.getBranchCode())
                .active(branch.getActive())
                .address(
                        branch.getAddress() != null
                                ? AddressMapper.toResponse(branch.getAddress())
                                : null
                )
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
}