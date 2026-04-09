package com.gm.hrms.dto.request;

import com.gm.hrms.enums.RegularizationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegularizationReviewDTO {

    private RegularizationStatus status; // APPROVED or REJECTED
    private String remarks;
}