package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.SalarySlipDownloadApprovalDTO;
import com.gm.hrms.dto.request.SalarySlipDownloadRequestDTO;
import com.gm.hrms.dto.response.SalarySlipDownloadRequestResponseDTO;
import com.gm.hrms.service.EmailService;
import com.gm.hrms.email.util.DownloadPasswordGenerator;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.DownloadRequestStatus;
import com.gm.hrms.enums.SalarySlipStatus;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.report.model.PayslipReportData;
import com.gm.hrms.report.provider.PayslipDataProvider;
import com.gm.hrms.report.service.PayslipReportService;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.repository.SalarySlipDownloadRequestRepository;
import com.gm.hrms.repository.SalarySlipRepository;
import com.gm.hrms.service.SalarySlipDownloadRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SalarySlipDownloadRequestServiceImpl
        implements SalarySlipDownloadRequestService {

    private final SalarySlipDownloadRequestRepository requestRepo;
    private final SalarySlipRepository                slipRepo;
    private final PersonalInformationRepository       personalRepo;
    private final EmailService                        emailService;
    private final DownloadPasswordGenerator           passwordGenerator;
    private final PasswordEncoder                     passwordEncoder;
    private final PayslipDataProvider                 dataProvider;
    private final PayslipReportService                reportService;

    // Employee: raise a request

    @Override
    public SalarySlipDownloadRequestResponseDTO raiseRequest(
            SalarySlipDownloadRequestDTO dto) {

        // 1. Resolve the salary slip
        SalarySlip slip = slipRepo
                .findByPersonalInformationIdAndMonthAndYear(
                        dto.getPersonalId(), dto.getMonth(), dto.getYear())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No salary slip found for the given employee and period. "
                                + "Please ensure payroll has been run for "
                                + monthYearLabel(dto.getMonth(), dto.getYear()) + "."));

        // 2. Slip must be finalized before an employee can request it
        if (slip.getStatus() == SalarySlipStatus.DRAFT) {
            throw new InvalidRequestException(
                    "The salary slip for " + monthYearLabel(dto.getMonth(), dto.getYear())
                            + " has not been finalized yet. "
                            + "Please contact HR to finalize payroll before requesting a download.");
        }

        // 3. Prevent duplicate live requests
        requestRepo.findActiveBySlipId(slip.getId()).ifPresent(existing -> {
            String statusLabel = existing.getStatus() == DownloadRequestStatus.PENDING
                    ? "already pending admin approval"
                    : "already approved — please check your email for the download password";
            throw new DuplicateResourceException(
                    "A download request for " + monthYearLabel(dto.getMonth(), dto.getYear())
                            + " is " + statusLabel + ".");
        });

        PersonalInformation personal = personalRepo.findById(dto.getPersonalId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found."));

        SalarySlipDownloadRequest request = SalarySlipDownloadRequest.builder()
                .salarySlip(slip)
                .personalInformation(personal)
                .month(dto.getMonth())
                .year(dto.getYear())
                .status(DownloadRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        requestRepo.save(request);
        log.info("Download request raised: slipId={}, personalId={}, period={}",
                slip.getId(), dto.getPersonalId(),
                monthYearLabel(dto.getMonth(), dto.getYear()));

        return toResponse(request);
    }

    // ── Employee: check status

    @Override
    @Transactional(readOnly = true)
    public SalarySlipDownloadRequestResponseDTO getStatusBySlipId(Long slipId) {
        SalarySlipDownloadRequest request =
                requestRepo.findActiveBySlipId(slipId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No active download request found for slip ID " + slipId
                                        + ". Please raise a new request."));
        return toResponse(request);
    }

    // ── Employee: history
    @Override
    @Transactional(readOnly = true)
    public List<SalarySlipDownloadRequestResponseDTO> getMyRequests(Long personalId) {
        return requestRepo
                .findByPersonalInformationIdOrderByRequestedAtDesc(personalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Admin: pending list ───────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<SalarySlipDownloadRequestResponseDTO> getPendingRequests() {
        return requestRepo
                .findByStatusOrderByRequestedAtAsc(DownloadRequestStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Admin: all requests ───────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<SalarySlipDownloadRequestResponseDTO> getAllRequests() {
        return requestRepo.findAllByOrderByRequestedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Admin: approve / reject ───────────────────────────────────────────

    @Override
    public SalarySlipDownloadRequestResponseDTO resolveRequest(
            Long requestId,
            SalarySlipDownloadApprovalDTO dto) {

        SalarySlipDownloadRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Download request with ID " + requestId + " not found."));

        if (request.getStatus() != DownloadRequestStatus.PENDING) {
            throw new InvalidRequestException(
                    "Request ID " + requestId + " is already in status '"
                            + request.getStatus() + "' and cannot be resolved again.");
        }

        PersonalInformation personal = request.getPersonalInformation();
        String employeeName = personal.getFirstName() + " " + personal.getLastName();
        String periodLabel  = monthYearLabel(request.getMonth(), request.getYear());
        String employeeEmail = resolveEmail(personal);

        if (Boolean.TRUE.equals(dto.getApproved())) {
            // ── Approve ──────────────────────────────────────────────────
            String rawPassword  = passwordGenerator.generate();
            String hashedPwd    = passwordEncoder.encode(rawPassword);

            request.setStatus(DownloadRequestStatus.APPROVED);
            request.setPasswordHash(hashedPwd);
            request.setResolvedAt(LocalDateTime.now());
            requestRepo.save(request);

            // Fire email asynchronously
            emailService.sendDownloadApprovalEmail(
                    employeeEmail, employeeName, periodLabel, rawPassword);

            log.info("Download request APPROVED: requestId={}, employee={}, period={}",
                    requestId, employeeEmail, periodLabel);

        } else {
            // ── Reject ───────────────────────────────────────────────────
            String reason = dto.getRejectionReason();
            if (reason == null || reason.isBlank()) {
                reason = "No reason provided.";
            }

            request.setStatus(DownloadRequestStatus.REJECTED);
            request.setRejectionReason(reason);
            request.setResolvedAt(LocalDateTime.now());
            requestRepo.save(request);

            // Fire email asynchronously
            emailService.sendDownloadRejectionEmail(
                    employeeEmail, employeeName, periodLabel, reason);

            log.info("Download request REJECTED: requestId={}, employee={}, period={}, reason={}",
                    requestId, employeeEmail, periodLabel, reason);
        }

        return toResponse(request);
    }

    // ── Employee: verify password + download ─────────────────────────────

    @Override
    public byte[] verifyAndDownload(Long slipId, String plainPassword) {

        // 1. Find the APPROVED request for this slip
        SalarySlipDownloadRequest request =
                requestRepo.findBySalarySlipIdAndStatus(
                                slipId, DownloadRequestStatus.APPROVED)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No approved download request found for slip ID " + slipId
                                        + ". Either the request has not been approved yet, "
                                        + "or the slip has already been downloaded."));

        // 2. Verify the one-time password
        if (!passwordEncoder.matches(plainPassword, request.getPasswordHash())) {
            log.warn("Incorrect download password supplied for requestId={}",
                    request.getId());
            throw new InvalidRequestException(
                    "The password you entered is incorrect. "
                            + "Please check your approval email and try again.");
        }

        // 3. The slip must still be in GENERATED / DOWNLOADED status (not DRAFT)
        SalarySlip slip = request.getSalarySlip();
        if (slip.getStatus() == SalarySlipStatus.DRAFT) {
            throw new IllegalStateException(
                    "The salary slip is still in DRAFT. "
                            + "Admin must finalize payroll before it can be downloaded.");
        }

        // 4. Generate PDF
        PayslipReportData reportData = dataProvider.buildFrom(slip);
        byte[] pdf = reportService.generatePdf(reportData);

        // 5. Mark request as DOWNLOADED — prevents future downloads
        request.setStatus(DownloadRequestStatus.DOWNLOADED);
        requestRepo.save(request);

        // 6. Update slip status
        slip.setStatus(SalarySlipStatus.DOWNLOADED);
        slipRepo.save(slip);

        log.info("Salary slip downloaded: slipId={}, requestId={}", slipId, request.getId());
        return pdf;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private SalarySlipDownloadRequestResponseDTO toResponse(
            SalarySlipDownloadRequest r) {

        PersonalInformation pi = r.getPersonalInformation();
        String employeeName = pi.getFirstName() + " " + pi.getLastName();
        String deptName = "";
        String empCode  = "";

        if (pi.getWorkProfile() != null) {
            if (pi.getWorkProfile().getDepartment() != null) {
                deptName = pi.getWorkProfile().getDepartment().getName();
            }
        }

        // Best-effort employee code from the associated Employee entity
        if (r.getSalarySlip() != null) {
            empCode = r.getSalarySlip().getEmployeeCode();
        }

        return SalarySlipDownloadRequestResponseDTO.builder()
                .id(r.getId())
                .slipId(r.getSalarySlip() != null ? r.getSalarySlip().getId() : null)
                .personalId(pi.getId())
                .employeeName(employeeName)
                .employeeCode(empCode)
                .department(deptName)
                .month(r.getMonth())
                .year(r.getYear())
                .monthYear(monthYearLabel(r.getMonth(), r.getYear()))
                .status(r.getStatus())
                .downloadEnabled(r.getStatus() == DownloadRequestStatus.APPROVED)
                .requestedAt(r.getRequestedAt())
                .resolvedAt(r.getResolvedAt())
                .rejectionReason(r.getRejectionReason())
                .build();
    }

    private static String monthYearLabel(int month, int year) {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;
    }

    /**
     * Resolves the employee's contact email.
     * Prefers office email; falls back to personal email.
     */
    private static String resolveEmail(PersonalInformation personal) {
        if (personal.getContact() == null) {
            throw new InvalidRequestException(
                    "Employee profile for '" + personal.getFirstName() + " "
                            + personal.getLastName()
                            + "' has no contact information. Cannot send approval email.");
        }
        String officeEmail = personal.getContact().getOfficeEmail();
        if (officeEmail != null && !officeEmail.isBlank()) return officeEmail;

        String personalEmail = personal.getContact().getPersonalEmail();
        if (personalEmail != null && !personalEmail.isBlank()) return personalEmail;

        throw new InvalidRequestException(
                "No valid email address found for employee '"
                        + personal.getFirstName() + " " + personal.getLastName()
                        + "'. Please update the employee's contact details.");
    }
}