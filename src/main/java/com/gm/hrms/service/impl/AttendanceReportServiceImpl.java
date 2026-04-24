package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.AttendanceReportFilterDTO;
import com.gm.hrms.dto.request.RegularizationRequestDTO;
import com.gm.hrms.dto.request.RegularizationReviewDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.*;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AttendanceReportMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.AttendanceReportService;
import com.gm.hrms.service.UserCodeResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceReportServiceImpl implements AttendanceReportService {

    private final AttendanceReportRepository         reportRepository;
    private final AttendanceRegularizationRepository regularizationRepository;
    private final AttendanceRepository               attendanceRepository;
    private final PersonalInformationRepository      personalRepository;
    private final ShiftRepository                    shiftRepository;
    private final UserCodeResolverService            codeResolver;

    // ======================================================
    // 1. DAILY ATTENDANCE REPORT
    // ======================================================
    @Override
    public ReportResponseDTO<DailyAttendanceReportDTO> getDailyReport(
            AttendanceReportFilterDTO filter, Pageable pageable) {

        LocalDate date = resolveDate(filter);

        Page<Attendance> page = reportRepository.findDailyReport(
                date,
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId()),
                safeId(filter.getShiftId()),
                safeId(filter.getBranchId()),
                filter.getStatus(),
                pageable
        );

        List<Long> personIds = page.getContent().stream()
                .map(a -> a.getPersonalInformation().getId())
                .toList();

        Map<Long, UserCodeDTO> codes = codeResolver.resolveAll(personIds);

        List<DailyAttendanceReportDTO> content = page.getContent().stream()
                .filter(a -> filter.getPersonalInformationId() == null
                        || a.getPersonalInformation().getId()
                        .equals(filter.getPersonalInformationId()))
                .map(a -> AttendanceReportMapper.toDailyDTO(
                        a,
                        codes.get(a.getPersonalInformation().getId()),
                        codeResolver))
                .toList();

        return ReportResponseDTO.<DailyAttendanceReportDTO>builder()
                .summary(buildSummary(page.getContent()))
                .data(toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 2. MONTHLY SUMMARY
    // ======================================================
    @Override
    public ReportResponseDTO<MonthlyAttendanceSummaryDTO> getMonthlySummary(
            AttendanceReportFilterDTO filter, Pageable pageable) {

        int month = filter.getMonth() != null ? filter.getMonth() : LocalDate.now().getMonthValue();
        int year  = filter.getYear()  != null ? filter.getYear()  : LocalDate.now().getYear();

        List<Attendance> records = reportRepository.findMonthlyAttendance(
                month, year,
                safeId(filter.getPersonalInformationId()),
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId())
        );

        Map<Long, List<Attendance>> byPerson = records.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getPersonalInformation().getId()));

        List<Long> personIds = new ArrayList<>(byPerson.keySet());
        Map<Long, UserCodeDTO> codes = codeResolver.resolveAll(personIds);

        List<MonthlyAttendanceSummaryDTO> summaries = byPerson.entrySet().stream()
                .map(e -> buildMonthlySummary(
                        e.getKey(),
                        e.getValue(),
                        month,
                        year,
                        codes.get(e.getKey())))
                .toList();

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), summaries.size());
        List<MonthlyAttendanceSummaryDTO> paged =
                summaries.subList(Math.min(start, summaries.size()), end);

        Page<MonthlyAttendanceSummaryDTO> page =
                new PageImpl<>(paged, pageable, summaries.size());

        return ReportResponseDTO.<MonthlyAttendanceSummaryDTO>builder()
                .summary(buildSummary(records))
                .data(toPageResponse(paged, page))
                .build();
    }

    // ======================================================
    // 3. EMPLOYEE-WISE ATTENDANCE DETAIL
    // ======================================================
    @Override
    public ReportResponseDTO<EmployeeAttendanceDetailDTO> getEmployeeAttendance(
            AttendanceReportFilterDTO filter, Pageable pageable) {

        Long personalId = filter.getPersonalInformationId();
        if (personalId == null) {
            throw new InvalidRequestException(
                    "personalInformationId is required for this report");
        }

        LocalDate from = safeFrom(filter.getFromDate());
        LocalDate to   = safeTo(filter.getToDate());

        Page<Attendance> page =
                reportRepository.findEmployeeAttendance(personalId, from, to, pageable);

        Set<Long> pendingRegIds = page.getContent().stream()
                .filter(a -> regularizationRepository.existsByAttendanceIdAndStatus(
                        a.getId(), RegularizationStatus.PENDING))
                .map(Attendance::getId)
                .collect(Collectors.toSet());

        List<EmployeeAttendanceDetailDTO> content = page.getContent().stream()
                .map(a -> AttendanceReportMapper.toDetailDTO(
                        a, pendingRegIds.contains(a.getId())))
                .toList();

        return ReportResponseDTO.<EmployeeAttendanceDetailDTO>builder()
                .summary(buildSummary(page.getContent()))
                .data(toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 4. ABSENT REPORT
    // ======================================================
    @Override
    public ReportResponseDTO<AbsentReportDTO> getAbsentReport(
            AttendanceReportFilterDTO filter, Pageable pageable) {

        LocalDate from = filter.getFromDate() != null ? filter.getFromDate() : LocalDate.now();
        LocalDate to   = filter.getToDate()   != null ? filter.getToDate()   : from;

        List<AbsentReportDTO> allAbsent = new ArrayList<>();

        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {

            final LocalDate currentDate = d;

            List<Long> absentIds = reportRepository.findAbsentPersonIds(
                    currentDate,
                    safeId(filter.getDepartmentId()),
                    safeId(filter.getDesignationId()));

            if (absentIds.isEmpty()) continue;

            Map<Long, UserCodeDTO> codes = codeResolver.resolveAll(absentIds);

            for (Long pid : absentIds) {
                personalRepository.findById(pid).ifPresent(p -> {
                    WorkProfile  wp    = p.getWorkProfile();
                    UserCodeDTO  c     = codes.get(p.getId());
                    allAbsent.add(AbsentReportDTO.builder()
                            .personalInformationId(p.getId())
                            .employeeCode(c.getEmployeeCode())
                            .traineeCode(c.getTraineeCode())
                            .internCode(c.getInternCode())
                            .employeeName(fullName(p))
                            .department(wp != null && wp.getDepartment() != null
                                    ? wp.getDepartment().getName() : null)
                            .designation(wp != null && wp.getDesignation() != null
                                    ? wp.getDesignation().getName() : null)
                            .absentDate(currentDate)
                            .build());
                });
            }
        }

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), allAbsent.size());
        List<AbsentReportDTO> paged =
                allAbsent.subList(Math.min(start, allAbsent.size()), end);

        Page<AbsentReportDTO> page = new PageImpl<>(paged, pageable, allAbsent.size());

        AttendanceReportSummaryDTO summary = AttendanceReportSummaryDTO.builder()
                .totalRecords(allAbsent.size())
                .totalAbsent(allAbsent.size())
                .build();

        return ReportResponseDTO.<AbsentReportDTO>builder()
                .summary(summary)
                .data(toPageResponse(paged, page))
                .build();
    }

    // ======================================================
    // 5. LATE COMING REPORT
    // ======================================================
    @Override
    public ReportResponseDTO<LateComingReportDTO> getLateComingReport(
            AttendanceReportFilterDTO filter, Pageable pageable) {

        Page<Attendance> page = reportRepository.findLateComingReport(
                safeFrom(filter.getFromDate()),
                safeTo(filter.getToDate()),
                safeId(filter.getPersonalInformationId()),
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId()),
                pageable
        );

        List<Long> personIds = page.getContent().stream()
                .map(a -> a.getPersonalInformation().getId())
                .toList();

        Map<Long, UserCodeDTO> codes = codeResolver.resolveAll(personIds);

        List<LateComingReportDTO> content = page.getContent().stream()
                .map(a -> AttendanceReportMapper.toLateDTO(
                        a,
                        codes.get(a.getPersonalInformation().getId())))
                .toList();

        AttendanceReportSummaryDTO summary = AttendanceReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .totalLate(page.getTotalElements())
                .build();

        return ReportResponseDTO.<LateComingReportDTO>builder()
                .summary(summary)
                .data(toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 6. OVERTIME REPORT
    // ======================================================
    @Override
    public ReportResponseDTO<OvertimeReportDTO> getOvertimeReport(
            AttendanceReportFilterDTO filter, Pageable pageable) {

        Page<Attendance> page = reportRepository.findOvertimeReport(
                safeFrom(filter.getFromDate()),
                safeTo(filter.getToDate()),
                safeId(filter.getPersonalInformationId()),
                safeId(filter.getDepartmentId()),
                safeId(filter.getDesignationId()),
                pageable
        );

        List<Long> personIds = page.getContent().stream()
                .map(a -> a.getPersonalInformation().getId())
                .toList();

        Map<Long, UserCodeDTO> codes = codeResolver.resolveAll(personIds);

        List<OvertimeReportDTO> content = page.getContent().stream()
                .map(a -> AttendanceReportMapper.toOvertimeDTO(
                        a,
                        codes.get(a.getPersonalInformation().getId())))
                .toList();

        AttendanceReportSummaryDTO summary = AttendanceReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .totalOvertime(page.getTotalElements())
                .build();

        return ReportResponseDTO.<OvertimeReportDTO>builder()
                .summary(summary)
                .data(toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 7. REGULARIZATION REPORT
    // ======================================================
    @Override
    public ReportResponseDTO<RegularizationReportDTO> getRegularizationReport(
            AttendanceReportFilterDTO filter, Pageable pageable) {

        Page<AttendanceRegularization> page = regularizationRepository.findWithFilters(
                null,
                safeFrom(filter.getFromDate()),
                safeTo(filter.getToDate()),
                safeId(filter.getPersonalInformationId()),
                pageable
        );

        List<Long> personIds = page.getContent().stream()
                .map(r -> r.getRequestedBy().getId())
                .toList();

        Map<Long, UserCodeDTO> codes = codeResolver.resolveAll(personIds);

        List<RegularizationReportDTO> content = page.getContent().stream()
                .map(r -> {
                    RegularizationReportDTO dto =
                            AttendanceReportMapper.toRegularizationDTO(r);
                    UserCodeDTO c = codes.get(r.getRequestedBy().getId());
                    dto.setEmployeeCode(c.getEmployeeCode());
                    dto.setTraineeCode(c.getTraineeCode());
                    dto.setInternCode(c.getInternCode());
                    return dto;
                })
                .toList();

        AttendanceReportSummaryDTO summary = AttendanceReportSummaryDTO.builder()
                .totalRecords(page.getTotalElements())
                .build();

        return ReportResponseDTO.<RegularizationReportDTO>builder()
                .summary(summary)
                .data(toPageResponse(content, page))
                .build();
    }

    // ======================================================
    // 8. SHIFT-WISE REPORT
    // ======================================================
    @Override
    public List<ShiftAttendanceReportDTO> getShiftWiseReport(
            AttendanceReportFilterDTO filter) {

        LocalDate date = resolveDate(filter);

        List<Shift> shifts = (filter.getShiftId() != null)
                ? List.of(shiftRepository.findById(filter.getShiftId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Shift not found")))
                : shiftRepository.findAll();

        return shifts.stream().map(shift -> {

            List<Attendance> records =
                    reportRepository.findShiftWiseAttendance(shift.getId(), date);

            List<Long> personIds = records.stream()
                    .map(a -> a.getPersonalInformation().getId())
                    .toList();

            Map<Long, UserCodeDTO> codes = codeResolver.resolveAll(personIds);

            List<DailyAttendanceReportDTO> dtos = records.stream()
                    .map(a -> AttendanceReportMapper.toDailyDTO(
                            a,
                            codes.get(a.getPersonalInformation().getId()),
                            codeResolver))
                    .toList();

            long present = records.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT
                            || a.getStatus() == AttendanceStatus.HALF_DAY)
                    .count();

            long late = records.stream()
                    .filter(a -> a.getCalculation() != null
                            && a.getCalculation().getLateMinutes() != null
                            && a.getCalculation().getLateMinutes() > 0)
                    .count();

            return ShiftAttendanceReportDTO.builder()
                    .shiftId(shift.getId())
                    .shiftName(shift.getShiftName())
                    .shiftType(shift.getShiftType().name())
                    .totalEmployees(records.size())
                    .presentCount(present)
                    .absentCount(records.size() - present)
                    .lateCount(late)
                    .records(dtos)
                    .build();

        }).toList();
    }

    // ======================================================
    // REGULARIZATION — SUBMIT
    // ======================================================
    @Override
    @Transactional
    public RegularizationReportDTO submitRegularization(RegularizationRequestDTO dto) {

        if (dto.getAttendanceId() == null) {
            throw new InvalidRequestException("attendanceId is required");
        }

        Attendance attendance = attendanceRepository
                .findById(dto.getAttendanceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendance not found"));

        if (regularizationRepository.existsByAttendanceIdAndStatus(
                dto.getAttendanceId(), RegularizationStatus.PENDING)) {
            throw new InvalidRequestException(
                    "A pending regularization already exists for this attendance");
        }

        PersonalInformation requester = personalRepository
                .findById(dto.getPersonalInformationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Person not found"));

        AttendanceRegularization reg = AttendanceRegularization.builder()
                .attendance(attendance)
                .requestedBy(requester)
                .originalCheckIn(attendance.getCheckIn())
                .originalCheckOut(attendance.getCheckOut())
                .requestedCheckIn(dto.getRequestedCheckIn())
                .requestedCheckOut(dto.getRequestedCheckOut())
                .reason(dto.getReason())
                .status(RegularizationStatus.PENDING)
                .build();

        AttendanceRegularization saved = regularizationRepository.save(reg);

        RegularizationReportDTO result =
                AttendanceReportMapper.toRegularizationDTO(saved);

        UserCodeDTO codes = codeResolver.resolve(requester.getId());
        result.setEmployeeCode(codes.getEmployeeCode());
        result.setTraineeCode(codes.getTraineeCode());
        result.setInternCode(codes.getInternCode());

        return result;
    }

    // ======================================================
    // REGULARIZATION — REVIEW
    // ======================================================
    @Override
    @Transactional
    public RegularizationReportDTO reviewRegularization(
            Long regularizationId, RegularizationReviewDTO dto) {

        if (dto.getStatus() == null
                || dto.getStatus() == RegularizationStatus.PENDING) {
            throw new InvalidRequestException(
                    "Status must be APPROVED or REJECTED");
        }

        AttendanceRegularization reg = regularizationRepository
                .findById(regularizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Regularization not found"));

        if (reg.getStatus() != RegularizationStatus.PENDING) {
            throw new InvalidRequestException(
                    "Only pending regularizations can be reviewed");
        }

        reg.setStatus(dto.getStatus());
        reg.setRemarks(dto.getRemarks());
        reg.setReviewedAt(LocalDateTime.now());

        if (dto.getStatus() == RegularizationStatus.APPROVED) {
            Attendance attendance = reg.getAttendance();
            if (reg.getRequestedCheckIn()  != null)
                attendance.setCheckIn(reg.getRequestedCheckIn());
            if (reg.getRequestedCheckOut() != null)
                attendance.setCheckOut(reg.getRequestedCheckOut());
            attendanceRepository.save(attendance);
        }

        AttendanceRegularization saved = regularizationRepository.save(reg);

        RegularizationReportDTO result =
                AttendanceReportMapper.toRegularizationDTO(saved);

        UserCodeDTO codes = codeResolver.resolve(reg.getRequestedBy().getId());
        result.setEmployeeCode(codes.getEmployeeCode());
        result.setTraineeCode(codes.getTraineeCode());
        result.setInternCode(codes.getInternCode());

        return result;
    }

    // ======================================================
    // PRIVATE HELPERS
    // ======================================================

    private LocalDate resolveDate(AttendanceReportFilterDTO filter) {
        return filter.getDate() != null ? filter.getDate() : LocalDate.now();
    }

    private AttendanceReportSummaryDTO buildSummary(List<Attendance> list) {
        return AttendanceReportSummaryDTO.builder()
                .totalRecords(list.size())
                .totalPresent(list.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count())
                .totalAbsent(list.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count())
                .totalHalfDay(list.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.HALF_DAY).count())
                .totalLeave(list.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.LEAVE).count())
                .totalHoliday(list.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.HOLIDAY).count())
                .totalLate(list.stream()
                        .filter(a -> a.getCalculation() != null
                                && a.getCalculation().getLateMinutes() != null
                                && a.getCalculation().getLateMinutes() > 0)
                        .count())
                .totalOvertime(list.stream()
                        .filter(a -> a.getCalculation() != null
                                && a.getCalculation().getOvertimeMinutes() != null
                                && a.getCalculation().getOvertimeMinutes() > 0)
                        .count())
                .build();
    }

    private MonthlyAttendanceSummaryDTO buildMonthlySummary(
            Long personalId,
            List<Attendance> records,
            int month,
            int year,
            UserCodeDTO codes) {

        PersonalInformation p  = records.get(0).getPersonalInformation();
        WorkProfile         wp = p.getWorkProfile();

        int totalWorkMinutes = records.stream()
                .filter(a -> a.getCalculation() != null
                        && a.getCalculation().getWorkMinutes() != null)
                .mapToInt(a -> a.getCalculation().getWorkMinutes())
                .sum();

        return MonthlyAttendanceSummaryDTO.builder()
                .personalInformationId(personalId)
                .employeeCode(codes.getEmployeeCode())
                .traineeCode(codes.getTraineeCode())
                .internCode(codes.getInternCode())
                .employeeName(fullName(p))
                .department(wp != null && wp.getDepartment()  != null
                        ? wp.getDepartment().getName()  : null)
                .designation(wp != null && wp.getDesignation() != null
                        ? wp.getDesignation().getName() : null)
                .month(month)
                .year(year)
                .totalPresent(records.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count())
                .totalAbsent(records.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count())
                .totalHalfDay(records.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.HALF_DAY).count())
                .totalLeave(records.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.LEAVE).count())
                .totalHoliday(records.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.HOLIDAY).count())
                .totalWeekOff(records.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.WEEK_OFF).count())
                .totalLate(records.stream()
                        .filter(a -> a.getCalculation() != null
                                && a.getCalculation().getLateMinutes() != null
                                && a.getCalculation().getLateMinutes() > 0)
                        .count())
                .totalOvertime(records.stream()
                        .filter(a -> a.getCalculation() != null
                                && a.getCalculation().getOvertimeMinutes() != null
                                && a.getCalculation().getOvertimeMinutes() > 0)
                        .count())
                .totalWorkMinutes(totalWorkMinutes)
                .build();
    }

    private String fullName(PersonalInformation p) {
        if (p == null) return "";
        String mid = (p.getMiddleName() != null && !p.getMiddleName().isBlank())
                ? p.getMiddleName() + " " : "";
        return p.getFirstName() + " " + mid + p.getLastName();
    }

    private <T> PageResponseDTO<T> toPageResponse(List<T> content, Page<?> page) {
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

    private Long safeId(Long id) {
        return id != null ? id : 0L;
    }

    private LocalDate safeFrom(LocalDate d) {
        return d != null ? d : LocalDate.of(2000, 1, 1);
    }

    private LocalDate safeTo(LocalDate d) {
        return d != null ? d : LocalDate.now();
    }
}
