package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;

import java.util.List;

public interface LeaveRequestService {

    // 🔥 APPLY LEAVE
    LeaveRequestResponseDTO apply(LeaveRequestDTO dto);

    // 🔥 APPROVAL FLOW
    void approve(Long leaveRequestId, Long approverId);

    void reject(Long leaveRequestId, String reason);

    // 🔥 CANCEL
    void cancel(Long leaveRequestId);

    // 🔥 GET
    List<LeaveRequestResponseDTO> getMyLeaves(Long personalId);

    void requestDocument(Long leaveRequestId);
}