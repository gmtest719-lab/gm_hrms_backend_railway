// com/gm/hrms/dto/response/ProjectStatusGroupReportDTO.java
package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectStatusGroupReportDTO {
    private ProjectStatus           status;
    private long                    count;
    private List<ProjectMasterReportDTO> projects;
}