package com.gm.hrms.service;

import com.gm.hrms.dto.request.SalarySlipDownloadApprovalDTO;
import com.gm.hrms.dto.request.SalarySlipDownloadRequestDTO;
import com.gm.hrms.dto.response.SalarySlipDownloadRequestResponseDTO;

import java.util.List;

public interface SalarySlipDownloadRequestService {

    // ── Employee operations ───────────────────────────────────────────────

    SalarySlipDownloadRequestResponseDTO raiseRequest(SalarySlipDownloadRequestDTO dto);

    SalarySlipDownloadRequestResponseDTO getStatusBySlipId(Long slipId);

    List<SalarySlipDownloadRequestResponseDTO> getMyRequests(Long personalId);

    // ── Admin operations ──────────────────────────────────────────────────

    List<SalarySlipDownloadRequestResponseDTO> getPendingRequests();

    List<SalarySlipDownloadRequestResponseDTO> getAllRequests();

    SalarySlipDownloadRequestResponseDTO resolveRequest(Long requestId,
                                                        SalarySlipDownloadApprovalDTO dto);

    // ── Download step ─────────────────────────────────────────────────────

    byte[] verifyAndDownload(Long slipId, String plainPassword);
}