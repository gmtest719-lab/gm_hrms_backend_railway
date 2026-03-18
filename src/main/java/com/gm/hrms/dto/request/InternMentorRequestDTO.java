package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InternMentorRequestDTO {

    @NotNull(message = "Mentor is required")
    private Long mentorEmployeeId;

    // Optional (har intern ka supervisor nahi hota)
    private Long supervisorEmployeeId;
}