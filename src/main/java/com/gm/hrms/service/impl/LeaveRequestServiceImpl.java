package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.*;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeaveRequestMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.LeaveBalanceService;
import com.gm.hrms.service.LeaveRequestService;
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

    private final LeaveRequestRepository repository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveApplicationRuleRepository ruleRepository;
    private final LeavePolicyRepository policyRepository;
    private final LeavePolicyLeaveTypeRepository mappingRepository;
    private final LeaveBalanceService balanceService;
    private final HolidayRepository holidayRepository;
    private final AttendanceRepository attendanceRepository;

    // ================= APPLY =================
    @Override
    public LeaveRequestResponseDTO apply(LeaveRequestDTO dto) {

        validateOverlap(dto);

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        Long policyId = getPolicyId(dto.getPersonalId());

        LeaveApplicationRule rule = ruleRepository
                .findByLeavePolicyIdAndIsActiveTrue(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Application rule not found"));

        validatePolicyMapping(policyId, dto.getLeaveTypeId());

        double totalDays = calculateDays(dto, rule);

        validateRules(dto, rule, totalDays);

        // 🔥 Deduct balance
        balanceService.deductLeave(
                dto.getPersonalId(),
                leaveType.getId(),
                (int) Math.ceil(totalDays)
        );

        LeaveRequest entity = LeaveRequestMapper.toEntity(dto, leaveType);
        entity.setTotalDays(totalDays);

        LeaveRequest saved = repository.save(entity);

        return LeaveRequestMapper.toResponse(saved);
    }

    // ================= APPROVE =================
    @Override
    public void approve(Long id, Long approverId) {

        LeaveRequest req = get(id);

        if (req.getStatus() == LeaveStatus.REJECTED || req.getStatus() == LeaveStatus.CANCELLED) {
            throw new InvalidRequestException("Cannot approve this request");
        }

        req.setApprovalLevel(req.getApprovalLevel() + 1);

        // 🔥 multi-level approval (2 level example)
        if (req.getApprovalLevel() >= 2) {
            req.setStatus(LeaveStatus.APPROVED);
            req.setApprovedBy(approverId);
            req.setApprovedAt(LocalDateTime.now());

            applyAttendance(req);
        }

        repository.save(req);
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

        // 🔥 restore balance
        balanceService.restoreLeave(
                req.getPersonalId(),
                req.getLeaveType().getId(),
                (int) Math.ceil(req.getTotalDays())
        );

        repository.save(req);
    }

    // ================= CANCEL =================
    @Override
    public void cancel(Long id) {

        LeaveRequest req = get(id);

        if (req.getStatus() == LeaveStatus.CANCELLED) {
            throw new InvalidRequestException("Already cancelled");
        }

        req.setStatus(LeaveStatus.CANCELLED);
        req.setIsCancelled(true);
        req.setCancelledAt(LocalDateTime.now());

        // 🔥 restore balance
        balanceService.restoreLeave(
                req.getPersonalId(),
                req.getLeaveType().getId(),
                (int) Math.ceil(req.getTotalDays())
        );

        repository.save(req);
    }

    // ================= GET =================
    @Override
    public List<LeaveRequestResponseDTO> getMyLeaves(Long personalId) {

        return repository.findByPersonalId(personalId)
                .stream()
                .map(LeaveRequestMapper::toResponse)
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

    private void validateRules(LeaveRequestDTO dto, LeaveApplicationRule rule, double days) {

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new InvalidRequestException("Invalid date range");
        }

        if (days < rule.getMinLeaveDuration()) {
            throw new InvalidRequestException("Below minimum leave duration");
        }

        if (days > rule.getMaxConsecutiveDays()) {
            throw new InvalidRequestException("Exceeded max consecutive days");
        }

        if (!rule.getAllowBackdatedLeave() && dto.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Backdated leave not allowed");
        }
    }

    // ================= CALCULATION =================

    private double calculateDays(LeaveRequestDTO dto, LeaveApplicationRule rule) {

        List<LocalDate> holidays = holidayRepository.findAll()
                .stream()
                .filter(Holiday::getIsActive)
                .map(Holiday::getHolidayDate)
                .toList();

        double total = 0;
        LocalDate date = dto.getStartDate();

        while (!date.isAfter(dto.getEndDate())) {

            boolean isWeekend = date.getDayOfWeek().getValue() >= 6;
            boolean isHoliday = holidays.contains(date);

            if (!rule.getSandwichRuleEnabled() && (isWeekend || isHoliday)) {
                date = date.plusDays(1);
                continue;
            }

            total++;
            date = date.plusDays(1);
        }

        // 🔥 half-day logic
        if (dto.getStartDayType() != DayType.FULL) total -= 0.5;
        if (dto.getEndDayType() != DayType.FULL) total -= 0.5;

        return total;
    }

    // ================= ATTENDANCE =================

    private void applyAttendance(LeaveRequest req) {

        LocalDate date = req.getStartDate();

        while (!date.isAfter(req.getEndDate())) {

            Attendance attendance = attendanceRepository
                    .findByPersonalInformationIdAndAttendanceDate(
                            req.getPersonalId(),
                            date
                    )
                    .orElse(
                            Attendance.builder()
                                    .attendanceDate(date)
                                    .build()
                    );

            if (req.getTotalDays() == 0.5) {
                attendance.setStatus(AttendanceStatus.HALF_DAY);
            } else {
                attendance.setStatus(AttendanceStatus.LEAVE);
            }

            attendanceRepository.save(attendance);

            date = date.plusDays(1);
        }
    }

    // ================= COMMON =================

    private LeaveRequest get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
    }

    //  TODO: replace with actual WorkProfile → EmploymentType → Policy
    private Long getPolicyId(Long personalId) {
        return 1L;
    }

    @Override
    public void requestDocument(Long id) {

        LeaveRequest req = get(id);

        if (req.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidRequestException("Only pending leave can request document");
        }

        req.setStatus(LeaveStatus.WAITING_FOR_DOCUMENT);

        repository.save(req);
    }}