package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.entity.LeaveRequest;
import com.gm.hrms.entity.LeaveType;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.LeaveStatus;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.service.UserCodeResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaveRequestMapper {

    private final PersonalInformationRepository personalRepo;
    private final UserCodeResolverService codeResolver;

    // ================= TO ENTITY =================
    public LeaveRequest toEntity(LeaveRequestDTO dto, LeaveType leaveType) {
        return LeaveRequest.builder()
                .personalId(dto.getPersonalId())
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startDayType(dto.getStartDayType())
                .endDayType(dto.getEndDayType())
                .reason(dto.getReason())
                .status(LeaveStatus.PENDING)
                .isCancelled(false)
                .build();
    }

    // ================= TO RESPONSE =================
    public LeaveRequestResponseDTO toResponse(LeaveRequest e) {

        PersonalInformation p = personalRepo.findById(e.getPersonalId())
                .orElse(null);

        String name = "N/A";
        String code = "N/A";

        if (p != null) {
            name = getFullName(p);
            code = codeResolver.getCode(p.getId());
        }

        return LeaveRequestResponseDTO.builder()
                .id(e.getId())
                .employeeName(name)
                .employeeCode(code)
                .leaveType(e.getLeaveType().getName())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .dateRange(formatRange(e))
                .totalDays(e.getTotalDays())
                .reason(e.getReason())
                .status(e.getStatus().name())
                .appliedOn(e.getCreatedAt())
                .build();
    }

    private String getFullName(PersonalInformation p) {
        return p.getFirstName() + " " +
                (p.getMiddleName() != null ? p.getMiddleName() + " " : "") +
                p.getLastName();
    }

    private String formatRange(LeaveRequest e) {
        return e.getStartDate() + " to " + e.getEndDate();
    }
}