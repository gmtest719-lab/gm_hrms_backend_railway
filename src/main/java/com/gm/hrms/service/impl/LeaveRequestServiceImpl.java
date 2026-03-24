package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.*;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeaveRequestMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.LeaveAttendanceService;
import com.gm.hrms.service.LeaveBalanceService;
import com.gm.hrms.service.LeaveRequestService;
import com.gm.hrms.service.LeaveValidationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final WorkProfileRepository workProfileRepository;
    private final LeaveRequestRepository repository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveApplicationRuleRepository ruleRepository;
    private final LeavePolicyRepository policyRepository;
    private final LeavePolicyLeaveTypeRepository mappingRepository;
    private final LeaveBalanceService balanceService;
    private final LeaveAttendanceService leaveAttendanceService;
    private final LeaveValidationEngine validationEngine;
    private final LeaveRequestMapper mapper;

    // ================= APPLY =================
    @Override
    public LeaveRequestResponseDTO apply(LeaveRequestDTO dto) {

        validateOverlap(dto);

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        Long policyId = getPolicyId(dto.getPersonalId());

        LeavePolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        //  VALIDATION ENGINE (includes probation now)
        double totalDays = validationEngine.calculateTotalDays(dto, policyId);

        validationEngine.validateBeforeApply(dto, totalDays, policyId);

        validationEngine.validateAttendanceConflict(dto.getPersonalId(), dto);

        validatePolicyMapping(policyId, dto.getLeaveTypeId());

        LeaveRequest entity = mapper.toEntity(dto, leaveType);
        //  IMPORTANT FIELDS (same as before)
        entity.setLeavePolicy(policy);
        entity.setTotalDays(totalDays);
        entity.setStatus(LeaveStatus.PENDING);
        entity.setApprovalLevel(0);
        entity.setIsCancelled(false);

        //  SAVE FIRST (ONLY CHANGE)
        LeaveRequest saved = repository.save(entity);

        //  THEN deduct (FIXED ORDER)
        balanceService.deductLeave(
                dto.getPersonalId(),
                leaveType.getId(),
                totalDays
        );

        return mapper.toResponse(saved);    }

    // ================= APPROVE =================
    @Override
    public void approve(Long id, Long approverId) {

        LeaveRequest req = get(id);

        if (req.getStatus() == LeaveStatus.REJECTED || req.getStatus() == LeaveStatus.CANCELLED) {
            throw new InvalidRequestException("Cannot approve this request");
        }

        req.setApprovalLevel(req.getApprovalLevel() + 1);

        if (isFinalApproval(req)) {

            req.setStatus(LeaveStatus.APPROVED);
            req.setApprovedBy(approverId);
            req.setApprovedAt(LocalDateTime.now());

            //  Attendance sync
            leaveAttendanceService.markLeaveAttendance(req);
        }

        repository.save(req);
    }

    // ================= DYNAMIC APPROVAL =================
    private boolean isFinalApproval(LeaveRequest req) {

        int requiredLevels = 2; // unchanged

        return req.getApprovalLevel() >= requiredLevels;
    }

    // ================= PARTIAL CANCEL =================
    public void partialCancel(Long leaveId, LocalDate from, LocalDate to) {

        LeaveRequest req = get(leaveId);

        double cancelDays = calculateCancelDays(from, to);

        balanceService.restoreLeave(
                req.getPersonalId(),
                req.getLeaveType().getId(),
                req.getTotalDays() //  FIX
        );

        leaveAttendanceService.revertLeaveAttendance(req);
    }

    // ================= REJECT =================
    @Override
    public void reject(Long id, String reason) {

        LeaveRequest req = get(id);

        if (req.getStatus() == LeaveStatus.APPROVED) {
            throw new InvalidRequestException("Already approved");
        }

        req.setStatus(LeaveStatus.REJECTED);
        req.setRejectionReason(reason);

        balanceService.restoreLeave(
                req.getPersonalId(),
                req.getLeaveType().getId(),
                req.getTotalDays() //  FIX
        );
        repository.save(req);
    }

    // ================= CANCEL =================
    @Override
    public void cancel(Long id) {

        LeaveRequest req = get(id);

        req.setStatus(LeaveStatus.CANCELLED);
        req.setIsCancelled(true);
        req.setCancelledAt(LocalDateTime.now());

        balanceService.restoreLeave(
                req.getPersonalId(),
                req.getLeaveType().getId(),
                req.getTotalDays() // FIX
        );

        leaveAttendanceService.revertLeaveAttendance(req);

        repository.save(req);
    }

    // ================= GET =================
    @Override
    public List<LeaveRequestResponseDTO> getMyLeaves(Long personalId) {

        return repository.findByPersonalId(personalId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // ================= VALIDATION =================

    private void validateOverlap(LeaveRequestDTO dto) {

        boolean exists = repository
                .existsByPersonalIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        dto.getPersonalId(),
                        dto.getEndDate(),
                        dto.getStartDate()
                );

        if (exists) {
            throw new InvalidRequestException("Leave already exists in this date range");
        }
    }

    private void validatePolicyMapping(Long policyId, Long leaveTypeId) {

        mappingRepository
                .findByLeavePolicyIdAndLeaveTypeIdAndIsActiveTrue(policyId, leaveTypeId)
                .orElseThrow(() -> new InvalidRequestException("Leave type not allowed in policy"));
    }

    // ================= COMMON =================

    private LeaveRequest get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
    }

    private double calculateCancelDays(LocalDate from, LocalDate to) {

        double days = 0;
        LocalDate date = from;

        while (!date.isAfter(to)) {
            days++;
            date = date.plusDays(1);
        }

        return days;
    }

    // 🔥 SAME OLD METHOD (UNCHANGED)
    private Long getPolicyId(Long personalId) {

        WorkProfile profile = workProfileRepository
                .findByPersonalInformationId(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Work profile not found"));

        EmploymentType type = profile.getPersonalInformation().getEmploymentType();

        LeavePolicy policy = policyRepository
                .findByEmploymentTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new ResourceNotFoundException("Leave policy not found"));

        return policy.getId();
    }

    // ================= DOCUMENT REQUEST =================
    @Override
    public void requestDocument(Long id) {

        LeaveRequest req = get(id);

        if (req.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidRequestException("Only pending leave can request document");
        }

        req.setStatus(LeaveStatus.WAITING_FOR_DOCUMENT);

        repository.save(req);
    }
}