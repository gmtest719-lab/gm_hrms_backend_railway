package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BranchTreeNodeDTO {
    private Long id;
    private String branchName;
    private String branchCode;
    private Boolean active;
    private Long parentId;
    private List<BranchTreeNodeDTO> children;
}