package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.LeaveStatus;
import com.gm.hrms.repository.*;
import com.gm.hrms.security.LeaveReportSecurityService;
import com.gm.hrms.service.LeaveReportService;
import com.gm.hrms.service.UserCodeResolverService;
import com.gm.hrms.specification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveReportServiceImpl implements LeaveReportService {

    private final LeaveRequestRepository     leaveRequestRepository;
    private final LeaveBalanceRepository     leaveBalanceRepository;
    private final LeaveTransactionRepository leaveTransactionRepository;
    private final PersonalInformationRepository personalRepository;
    private final LeaveReportSecurityService securityService;
    private final UserCodeResolverService    codeResolver;

    // ================================================================
    // 1. LEAVE BALANCE REPORT
    // ================================================================
    @Override
    public LeaveReportResponseDTO<LeaveBalanceReportDTO> getBalanceReport(
            LeaveReportFilterDTO filter, Pageable pageable) {

        securityService.sanitizeFilterForRole(filter);
        Long personalId = securityService.enforcePersonalId(filter.getPersonalId());

        // default year to current if not specified
        if (filter.getYear() == null) {
            filter.setYear(java.time.LocalDate.now().getYear());
        }

        Page<LeaveBalance> page = leaveBalanceRepository.findAll(
                LeaveBalanceReportSpecification.filter(filter, personalId),
                pageable);

        List<LeaveBalanceReportDTO> content = page.getContent().stream()
                .map(this::toBalanceDTO)
                .toList();

        double totalRemaining = page.getContent().stream()
                .mapToDouble(b -> b.getRemainingLeaves() != null ? b.getRemainingLeaves() : 0)
                .sum();

        LeaveReportSummaryDTO summary = LeaveReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .totalRemainingBalance(totalRemaining)
                .build();

        return LeaveReportResponseDTO.<LeaveBalanceReportDTO>builder()
                .summary(summary)
                .data(toPage(content, page))
                .build();
    }

    // ================================================================
    // 2. LEAVE HISTORY REPORT
    // ================================================================
    @Override
    public LeaveReportResponseDTO<LeaveHistoryReportDTO> getHistoryReport(
            LeaveReportFilterDTO filter, Pageable pageable) {

        securityService.sanitizeFilterForRole(filter);
        Long personalId = securityService.enforcePersonalId(filter.getPersonalId());

        Page<LeaveRequest> page = leaveRequestRepository.findAll(
                LeaveRequestReportSpecification.filter(filter, personalId),
                pageable);

        List<LeaveHistoryReportDTO> content = page.getContent().stream()
                .map(this::toHistoryDTO)
                .toList();

        return LeaveReportResponseDTO.<LeaveHistoryReportDTO>builder()
                .summary(buildRequestSummary(page.getContent(), page.getTotalElements()))
                .data(toPage(content, page))
                .build();
    }

    // ================================================================
    // 3. LEAVE REQUEST REPORT
    // ================================================================
    @Override
    public LeaveReportResponseDTO<LeaveRequestReportDTO> getRequestReport(
            LeaveReportFilterDTO filter, Pageable pageable) {

        securityService.sanitizeFilterForRole(filter);
        Long personalId = securityService.enforcePersonalId(filter.getPersonalId());

        Page<LeaveRequest> page = leaveRequestRepository.findAll(
                LeaveRequestReportSpecification.filter(filter, personalId),
                pageable);

        List<LeaveRequestReportDTO> content = page.getContent().stream()
                .map(this::toRequestDTO)
                .toList();

        return LeaveReportResponseDTO.<LeaveRequestReportDTO>builder()
                .summary(buildRequestSummary(page.getContent(), page.getTotalElements()))
                .data(toPage(content, page))
                .build();
    }

    // ================================================================
    // 4. LEAVE TYPE USAGE REPORT
    // ================================================================
    @Override
    public LeaveReportResponseDTO<LeaveTypeUsageReportDTO> getTypeUsageReport(
            LeaveReportFilterDTO filter, Pageable pageable) {

        securityService.sanitizeFilterForRole(filter);
        Long personalId = securityService.enforcePersonalId(filter.getPersonalId());

        // Fetch all matching leave requests (within date range / personal scope)
        List<LeaveRequest> all = leaveRequestRepository.findAll(
                LeaveRequestReportSpecification.filter(filter, personalId));

        Map<Long, List<LeaveRequest>> grouped = all.stream()
                .filter(r -> r.getLeaveType() != null)
                .collect(Collectors.groupingBy(r -> r.getLeaveType().getId()));

        List<LeaveTypeUsageReportDTO> usageList = grouped.entrySet().stream()
                .map(e -> {
                    LeaveType lt = e.getValue().get(0).getLeaveType();
                    List<LeaveRequest> requests = e.getValue();
                    return LeaveTypeUsageReportDTO.builder()
                            .leaveTypeId(lt.getId())
                            .leaveTypeName(lt.getName())
                            .leaveCode(lt.getCode())
                            .isPaid(Boolean.TRUE.equals(lt.getIsPaid()))
                            .totalRequests(requests.size())
                            .totalDaysTaken(requests.stream()
                                    .mapToDouble(r -> r.getTotalDays() != null ? r.getTotalDays() : 0)
                                    .sum())
                            .approvedCount(count(requests, LeaveStatus.APPROVED))
                            .pendingCount(count(requests, LeaveStatus.PENDING))
                            .rejectedCount(count(requests, LeaveStatus.REJECTED))
                            .cancelledCount(count(requests, LeaveStatus.CANCELLED))
                            .build();
                })
                .sorted(Comparator.comparingDouble(LeaveTypeUsageReportDTO::getTotalDaysTaken).reversed())
                .toList();

        // Manual pagination for in-memory list
        Page<LeaveTypeUsageReportDTO> page = toInMemoryPage(usageList, pageable);

        LeaveReportSummaryDTO summary = LeaveReportSummaryDTO.builder()
                .totalRecords(usageList.size())
                .totalDaysTaken(usageList.stream().mapToDouble(LeaveTypeUsageReportDTO::getTotalDaysTaken).sum())
                .approvedCount(usageList.stream().mapToLong(LeaveTypeUsageReportDTO::getApprovedCount).sum())
                .pendingCount(usageList.stream().mapToLong(LeaveTypeUsageReportDTO::getPendingCount).sum())
                .rejectedCount(usageList.stream().mapToLong(LeaveTypeUsageReportDTO::getRejectedCount).sum())
                .build();

        return LeaveReportResponseDTO.<LeaveTypeUsageReportDTO>builder()
                .summary(summary)
                .data(toPage(page.getContent(), page))
                .build();
    }

    // ================================================================
    // 5. LEAVE TRENDS REPORT
    // ================================================================
    @Override
    public LeaveReportResponseDTO<LeaveTrendsReportDTO> getTrendsReport(
            LeaveReportFilterDTO filter, Pageable pageable) {

        securityService.sanitizeFilterForRole(filter);
        Long personalId = securityService.enforcePersonalId(filter.getPersonalId());

        List<LeaveRequest> all = leaveRequestRepository.findAll(
                LeaveRequestReportSpecification.filter(filter, personalId));

        // Group by year-month
        Map<String, List<LeaveRequest>> grouped = all.stream()
                .filter(r -> r.getStartDate() != null)
                .collect(Collectors.groupingBy(r ->
                        r.getStartDate().getYear() + "-" + r.getStartDate().getMonthValue()));

        List<LeaveTrendsReportDTO> trendsList = grouped.entrySet().stream()
                .map(e -> {
                    String[] parts    = e.getKey().split("-");
                    int yr            = Integer.parseInt(parts[0]);
                    int mn            = Integer.parseInt(parts[1]);
                    List<LeaveRequest> reqs = e.getValue();

                    return LeaveTrendsReportDTO.builder()
                            .year(yr)
                            .month(mn)
                            .monthName(Month.of(mn).name().charAt(0)
                                    + Month.of(mn).name().substring(1).toLowerCase())
                            .totalRequests(reqs.size())
                            .totalDaysTaken(reqs.stream()
                                    .mapToDouble(r -> r.getTotalDays() != null ? r.getTotalDays() : 0)
                                    .sum())
                            .approvedCount(count(reqs, LeaveStatus.APPROVED))
                            .pendingCount(count(reqs, LeaveStatus.PENDING))
                            .rejectedCount(count(reqs, LeaveStatus.REJECTED))
                            .build();
                })
                .sorted(Comparator.comparingInt(LeaveTrendsReportDTO::getYear)
                        .thenComparingInt(LeaveTrendsReportDTO::getMonth))
                .toList();

        Page<LeaveTrendsReportDTO> page = toInMemoryPage(trendsList, pageable);

        LeaveReportSummaryDTO summary = LeaveReportSummaryDTO.builder()
                .totalRecords(trendsList.size())
                .totalDaysTaken(trendsList.stream().mapToDouble(LeaveTrendsReportDTO::getTotalDaysTaken).sum())
                .approvedCount(trendsList.stream().mapToLong(LeaveTrendsReportDTO::getApprovedCount).sum())
                .rejectedCount(trendsList.stream().mapToLong(LeaveTrendsReportDTO::getRejectedCount).sum())
                .build();

        return LeaveReportResponseDTO.<LeaveTrendsReportDTO>builder()
                .summary(summary)
                .data(toPage(page.getContent(), page))
                .build();
    }

    // ================================================================
    // 6. LEAVE ENCASHMENT REPORT
    // ================================================================
    @Override
    public LeaveReportResponseDTO<LeaveEncashmentReportDTO> getEncashmentReport(
            LeaveReportFilterDTO filter, Pageable pageable) {

        securityService.sanitizeFilterForRole(filter);
        Long personalId = securityService.enforcePersonalId(filter.getPersonalId());

        Page<LeaveTransaction> page = leaveTransactionRepository.findAll(
                LeaveTransactionReportSpecification.encashmentFilter(filter, personalId),
                pageable);

        List<LeaveEncashmentReportDTO> content = page.getContent().stream()
                .map(this::toEncashmentDTO)
                .toList();

        LeaveReportSummaryDTO summary = LeaveReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .totalDaysTaken(page.getContent().stream()
                        .mapToDouble(LeaveTransaction::getDays).sum())
                .build();

        return LeaveReportResponseDTO.<LeaveEncashmentReportDTO>builder()
                .summary(summary)
                .data(toPage(content, page))
                .build();
    }

    // ================================================================
    // 7. LEAVE APPROVAL REPORT
    // ================================================================
    @Override
    public LeaveReportResponseDTO<LeaveApprovalReportDTO> getApprovalReport(
            LeaveReportFilterDTO filter, Pageable pageable) {

        // For restricted roles: sanitize department/designation/approverId
        securityService.sanitizeFilterForRole(filter);
        Long personalId = securityService.enforcePersonalId(filter.getPersonalId());

        // Use spec-based query so department/designation filters work
        // AND we can enforce personalId row-level security
        // We also want APPROVED + REJECTED only → set status only if null
        LeaveReportFilterDTO approvalFilter = cloneWithApprovalDefaults(filter);

        Page<LeaveRequest> page = leaveRequestRepository.findAll(
                LeaveRequestReportSpecification.filter(approvalFilter, personalId),
                pageable);

        List<LeaveApprovalReportDTO> content = page.getContent().stream()
                .map(this::toApprovalDTO)
                .toList();

        LeaveReportSummaryDTO summary = LeaveReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .totalDaysTaken(page.getContent().stream()
                        .mapToDouble(r -> r.getTotalDays() != null ? r.getTotalDays() : 0).sum())
                .approvedCount(count(page.getContent(), LeaveStatus.APPROVED))
                .rejectedCount(count(page.getContent(), LeaveStatus.REJECTED))
                .build();

        return LeaveReportResponseDTO.<LeaveApprovalReportDTO>builder()
                .summary(summary)
                .data(toPage(content, page))
                .build();
    }

    // ================================================================
    // PRIVATE HELPERS
    // ================================================================

    private LeaveBalanceReportDTO toBalanceDTO(LeaveBalance b) {

        PersonalInformation p  = b.getPersonal();
        WorkProfile         wp = p != null ? p.getWorkProfile() : null;

        return LeaveBalanceReportDTO.builder()
                .personalId(p != null ? p.getId() : null)
                .employeeCode(p != null ? codeResolver.getCode(p.getId()) : "N/A")
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : null)
                .leaveType(b.getLeaveType() != null ? b.getLeaveType().getName() : null)
                .leaveCode(b.getLeaveType() != null ? b.getLeaveType().getCode() : null)
                .totalLeaves(b.getTotalLeaves())
                .usedLeaves(b.getUsedLeaves())
                .remainingLeaves(b.getRemainingLeaves())
                .year(b.getYear())
                .build();
    }

    private LeaveHistoryReportDTO toHistoryDTO(LeaveRequest r) {

        PersonalInformation p  = safePersonal(r.getPersonalId());
        WorkProfile         wp = p != null ? p.getWorkProfile() : null;

        return LeaveHistoryReportDTO.builder()
                .id(r.getId())
                .employeeCode(p != null ? codeResolver.getCode(p.getId()) : "N/A")
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : null)
                .leaveType(r.getLeaveType() != null ? r.getLeaveType().getName() : null)
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .totalDays(r.getTotalDays())
                .startDayType(r.getStartDayType() != null ? r.getStartDayType().name() : null)
                .endDayType(r.getEndDayType()   != null ? r.getEndDayType().name()   : null)
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .reason(r.getReason())
                .appliedOn(r.getCreatedAt())
                .build();
    }

    private LeaveRequestReportDTO toRequestDTO(LeaveRequest r) {

        PersonalInformation p  = safePersonal(r.getPersonalId());
        WorkProfile         wp = p != null ? p.getWorkProfile() : null;

        return LeaveRequestReportDTO.builder()
                .id(r.getId())
                .employeeCode(p != null ? codeResolver.getCode(p.getId()) : "N/A")
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : null)
                .leaveType(r.getLeaveType() != null ? r.getLeaveType().getName() : null)
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .totalDays(r.getTotalDays())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .reason(r.getReason())
                .approvalLevel(r.getApprovalLevel())
                .appliedOn(r.getCreatedAt())
                .hasDocument(false) // document check can be enhanced later
                .build();
    }

    private LeaveEncashmentReportDTO toEncashmentDTO(LeaveTransaction t) {

        PersonalInformation p  = t.getPersonal();
        WorkProfile         wp = p != null ? p.getWorkProfile() : null;

        return LeaveEncashmentReportDTO.builder()
                .employeeCode(p != null ? codeResolver.getCode(p.getId()) : "N/A")
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : null)
                .leaveType(t.getLeaveBalance() != null && t.getLeaveBalance().getLeaveType() != null
                        ? t.getLeaveBalance().getLeaveType().getName() : null)
                .daysEncashed(t.getDays())
                .beforeBalance(t.getBeforeBalance())
                .afterBalance(t.getAfterBalance())
                .encashedOn(t.getTransactionDate())
                .remarks(t.getRemarks())
                .build();
    }

    private LeaveApprovalReportDTO toApprovalDTO(LeaveRequest r) {

        PersonalInformation p       = safePersonal(r.getPersonalId());
        PersonalInformation approver = r.getApprovedBy() != null ? safePersonal(r.getApprovedBy()) : null;
        WorkProfile         wp      = p != null ? p.getWorkProfile() : null;

        return LeaveApprovalReportDTO.builder()
                .requestId(r.getId())
                .employeeCode(p != null ? codeResolver.getCode(p.getId()) : "N/A")
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment()  != null ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null ? wp.getDesignation().getName() : null)
                .leaveType(r.getLeaveType() != null ? r.getLeaveType().getName() : null)
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .totalDays(r.getTotalDays())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .approverCode(approver != null ? codeResolver.getCode(approver.getId()) : null)
                .approverName(fullName(approver))
                .approvedAt(r.getApprovedAt())
                .rejectionReason(r.getRejectionReason())
                .appliedOn(r.getCreatedAt())
                .build();
    }

    // ── summary builders ──────────────────────────────────────────────
    private LeaveReportSummaryDTO buildRequestSummary(List<LeaveRequest> list, long total) {
        return LeaveReportSummaryDTO.builder()
                .totalRecords(total)
                .totalDaysTaken(list.stream()
                        .filter(r -> r.getStatus() == LeaveStatus.APPROVED)
                        .mapToDouble(r -> r.getTotalDays() != null ? r.getTotalDays() : 0)
                        .sum())
                .pendingCount(count(list, LeaveStatus.PENDING))
                .approvedCount(count(list, LeaveStatus.APPROVED))
                .rejectedCount(count(list, LeaveStatus.REJECTED))
                .cancelledCount(count(list, LeaveStatus.CANCELLED))
                .build();
    }

    // ── misc helpers ──────────────────────────────────────────────────
    private long count(List<LeaveRequest> list, LeaveStatus status) {
        return list.stream().filter(r -> r.getStatus() == status).count();
    }

    private PersonalInformation safePersonal(Long id) {
        if (id == null) return null;
        return personalRepository.findById(id).orElse(null);
    }

    private String fullName(PersonalInformation p) {
        if (p == null) return "N/A";
        String mid = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? p.getMiddleName() + " " : "";
        return p.getFirstName() + " " + mid + p.getLastName();
    }

    private <T> PageResponseDTO<T> toPage(List<T> content, Page<?> page) {
        return PageResponseDTO.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private <T> Page<T> toInMemoryPage(List<T> all, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), all.size());
        List<T> paged = all.subList(Math.min(start, all.size()), end);
        return new PageImpl<>(paged, pageable, all.size());
    }

    private LeaveReportFilterDTO cloneWithApprovalDefaults(LeaveReportFilterDTO src) {
        return LeaveReportFilterDTO.builder()
                .fromDate(src.getFromDate())
                .toDate(src.getToDate())
                .personalId(src.getPersonalId())
                .departmentId(src.getDepartmentId())
                .designationId(src.getDesignationId())
                .leaveTypeId(src.getLeaveTypeId())
                .status(src.getStatus())
                .approverId(src.getApproverId())
                .month(src.getMonth())
                .year(src.getYear())
                .build();
    }
}