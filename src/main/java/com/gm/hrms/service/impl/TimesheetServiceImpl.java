package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.TimesheetEntryDTO;
import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.TimesheetResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.*;
import com.gm.hrms.mapper.TimesheetMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.EmailService;
import com.gm.hrms.service.TimesheetService;
import com.gm.hrms.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {

    // ── Constants ─────────────────────────────────────────────────────────
    private static final int FULL_DAY_MINUTES  = 480; // 8 hours
    private static final int HALF_DAY_MINUTES  = 240; // 4 hours
    private static final int BUFFER_MINUTES    = 30;
    private static final int EDIT_WINDOW_DAYS  = 3;   // users can edit freely within 3 days

    // ── Dependencies ──────────────────────────────────────────────────────
    private final TimesheetRepository                timesheetRepository;
    private final PersonalInformationRepository      personRepository;
    private final ProjectRepository                  projectRepository;
    private final AttendanceRepository               attendanceRepository;
    private final AttendanceBreakLogRepository       breakLogRepository;
    private final LeaveRequestRepository             leaveRequestRepository;
    private final TimesheetAccessRequestRepository   accessRequestRepository;
    private final EmailService                       emailService;

    @Value("${hrms.timesheet.summary.email:hr@company.com}")
    private String timesheetSummaryEmail;

    // ═══════════════════════════════════════════════════════════════════════
    //  CREATE OR UPDATE (DRAFT / SAVE)
    // ═══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public TimesheetResponseDTO createOrUpdateTimesheet(TimesheetRequestDTO request) {

        LocalDate workDate = request.getWorkDate();
        LocalDate today    = LocalDate.now();
        boolean   isDraft  = request.getStatus() == TimesheetStatus.DRAFT;

        // ── 1. Check-in validation (for today only) ───────────────────────
        if (workDate.equals(today)) {
            Attendance todayAttendance = requireTodayCheckIn(request.getPersonId(), today);
            // ── 3. Leave restriction ──────────────────────────────────────
            validateNoFullDayLeave(request.getPersonId(), workDate);

            if (!isDraft) {
                // ── 6. Worked hours vs requested hours ────────────────────
                int actualWorkedMinutes = calculateActualWorkedMinutes(
                        todayAttendance, request.getPersonId());
                int requestedMinutes    = sumEntryMinutes(request.getEntries());
                validateWorkedHours(requestedMinutes, actualWorkedMinutes);

                // ── 7. Buffer rule ────────────────────────────────────────
                validateBufferRule(requestedMinutes, actualWorkedMinutes);
            }
        }

        // ── 5. Editing window ─────────────────────────────────────────────
        validateEditWindow(request.getPersonId(), workDate, today);

        PersonalInformation person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("Person not found"));

        Timesheet timesheet = timesheetRepository
                .findByPerson_IdAndWorkDate(request.getPersonId(), workDate)
                .orElse(Timesheet.builder()
                        .person(person)
                        .workDate(workDate)
                        .entries(new ArrayList<>())
                        .status(TimesheetStatus.DRAFT)
                        .isLocked(false)
                        .build());

        // ── Locked check (post-submission lock) ───────────────────────────
        if (Boolean.TRUE.equals(timesheet.getIsLocked())) {
            validateExtraWorkAccess(request.getPersonId(), workDate, today);
        }

        // ── 2. Half-day minimum 4-hour check ─────────────────────────────
        if (!isDraft) {
            validateHalfDayMinimum(request.getPersonId(), workDate, request.getEntries());
        }

        // ── Build entries ─────────────────────────────────────────────────
        timesheet.getEntries().clear();
        int totalMinutes = 0;

        if (request.getEntries() != null) {
            for (TimesheetEntryDTO entryDTO : request.getEntries()) {

                if (!isDraft) {
                    if (entryDTO.getProjectId() == null)
                        throw new RuntimeException("Project is required for each entry");
                    if (entryDTO.getWorkedTime() == null || entryDTO.getWorkedTime().isBlank())
                        throw new RuntimeException("Worked time is required for each entry");
                }

                // Skip incomplete entries in DRAFT
                if (isDraft && (entryDTO.getProjectId() == null
                        || entryDTO.getWorkedTime() == null)) {
                    continue;
                }

                Project project = projectRepository.findById(entryDTO.getProjectId())
                        .orElseThrow(() -> new RuntimeException("Project not found"));

                int minutes = TimeUtil.toMinutes(entryDTO.getWorkedTime());
                totalMinutes += minutes;

                timesheet.getEntries().add(
                        TimesheetEntry.builder()
                                .timesheet(timesheet)
                                .project(project)
                                .workedMinutes(minutes)
                                .taskName(entryDTO.getTaskName())
                                .description(entryDTO.getDescription())
                                .build()
                );
            }
        }

        if (!isDraft && totalMinutes > FULL_DAY_MINUTES) {
            throw new RuntimeException("Daily work hours cannot exceed 8 hours (480 minutes)");
        }

        timesheet.setTotalMinutes(totalMinutes);
        timesheet.setStatus(request.getStatus());

        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  SUBMIT  (Req 4 — lock + summary email)
    // ═══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public TimesheetResponseDTO submitTimesheet(Long timesheetId) {

        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        if (Boolean.TRUE.equals(timesheet.getIsLocked())) {
            // This is an extra-work re-submission — check access
            validateExtraWorkAccess(
                    timesheet.getPerson().getId(),
                    timesheet.getWorkDate(),
                    LocalDate.now());
        }

        timesheet.setStatus(TimesheetStatus.SUBMITTED);
        timesheet.setSubmittedAt(LocalDateTime.now());
        timesheet.setIsLocked(true);  // lock after submit

        Timesheet saved = timesheetRepository.save(timesheet);

        // ── Send summary email ────────────────────────────────────────────
        sendSubmissionEmail(saved, false);

        return TimesheetMapper.toResponse(saved);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  APPROVE / REJECT / GET / DELETE  (unchanged flow, kept clean)
    // ═══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public TimesheetResponseDTO approveTimesheet(Long timesheetId) {
        Timesheet timesheet = getOrThrow(timesheetId);
        timesheet.setStatus(TimesheetStatus.APPROVED);
        timesheet.setApprovedAt(LocalDateTime.now());
        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    @Override
    @Transactional
    public TimesheetResponseDTO rejectTimesheet(Long timesheetId) {
        Timesheet timesheet = getOrThrow(timesheetId);
        timesheet.setStatus(TimesheetStatus.REJECTED);
        timesheet.setIsLocked(false); // unlock so employee can re-submit
        return TimesheetMapper.toResponse(timesheetRepository.save(timesheet));
    }

    @Override
    public TimesheetResponseDTO getTimesheetById(Long id) {
        return TimesheetMapper.toResponse(getOrThrow(id));
    }

    @Override
    public TimesheetResponseDTO getByPersonAndDate(Long personId, String date) {
        return timesheetRepository
                .findByPerson_IdAndWorkDate(personId, LocalDate.parse(date))
                .map(TimesheetMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));
    }

    @Override
    public PageResponseDTO<TimesheetResponseDTO> getAllTimesheets(Pageable pageable) {
        Page<Timesheet> page = timesheetRepository.findAll(pageable);
        List<TimesheetResponseDTO> content = page.getContent()
                .stream().map(TimesheetMapper::toResponse).toList();
        return PageResponseDTO.<TimesheetResponseDTO>builder()
                .content(content).page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .first(page.isFirst()).last(page.isLast()).build();
    }

    @Override
    @Transactional
    public void deleteTimesheet(Long id) {
        timesheetRepository.delete(getOrThrow(id));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PRIVATE — VALIDATION HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * REQ 1 — Employee must be checked-in to fill today's timesheet.
     */
    private Attendance requireTodayCheckIn(Long personId, LocalDate today) {
        Attendance attendance = attendanceRepository
                .findByPersonalInformationIdAndAttendanceDate(personId, today)
                .orElseThrow(() -> new RuntimeException(
                        "You must check in before filling today's timesheet."));

        if (attendance.getCheckIn() == null) {
            throw new RuntimeException(
                    "You must check in before filling today's timesheet.");
        }
        return attendance;
    }

    /**
     * REQ 3 — Block timesheet creation if the employee is on a full-day approved leave.
     */
    private void validateNoFullDayLeave(Long personId, LocalDate date) {
        List<LeaveRequest> leaves = leaveRequestRepository
                .findApprovedLeavesOnDate(personId, date);

        for (LeaveRequest leave : leaves) {
            // A full-day leave spans the entire day (startDayType == FULL and endDayType == FULL,
            // or it is a multi-day leave with no half-day markers on these edges)
            boolean isFullDay =
                    (leave.getStartDayType() == DayType.FULL
                            || leave.getStartDayType() == null)
                            && (leave.getEndDayType() == DayType.FULL
                            || leave.getEndDayType() == null);

            if (isFullDay) {
                throw new RuntimeException(
                        "You are on an approved full-day leave on " + date +
                                ". Timesheet creation is not allowed.");
            }
        }
    }

    /**
     * REQ 2 — If the employee is on a half-day leave, the timesheet must log ≥ 4 hours.
     */
    private void validateHalfDayMinimum(Long personId, LocalDate workDate,
                                        List<TimesheetEntryDTO> entries) {
        if (entries == null) return;

        boolean isHalfDayLeave = leaveRequestRepository
                .findApprovedLeavesOnDate(personId, workDate)
                .stream()
                .anyMatch(lr ->
                        lr.getStartDayType() == DayType.FIRST_HALF
                                || lr.getStartDayType() == DayType.SECOND_HALF
                                || lr.getEndDayType() == DayType.FIRST_HALF
                                || lr.getEndDayType() == DayType.SECOND_HALF
                );

        if (isHalfDayLeave) {
            int totalMinutes = sumEntryMinutes(entries);
            if (totalMinutes < HALF_DAY_MINUTES) {
                throw new RuntimeException(
                        "You are on a half-day leave. Please log at least 4 hours (240 minutes) " +
                                "in your timesheet. Currently entered: " + TimeUtil.toHHmm(totalMinutes));
            }
        }
    }

    /**
     * REQ 5 — Users can edit freely within EDIT_WINDOW_DAYS days.
     * For older dates an approved TimesheetAccessRequest (EDIT_OLD) is required.
     */
    private void validateEditWindow(Long personId, LocalDate workDate, LocalDate today) {
        if (!workDate.isBefore(today.minusDays(EDIT_WINDOW_DAYS))) return; // within window — OK

        TimesheetAccessRequest access = accessRequestRepository
                .findByPerson_IdAndRequestedDateAndAccessTypeAndStatus(
                        personId, workDate,
                        TimesheetAccessType.EDIT_OLD,
                        TimesheetAccessStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException(
                        "Timesheets can only be edited within the last " + EDIT_WINDOW_DAYS + " days. "
                                + "Please submit an access request to Admin/HR to edit older timesheets."));

        if (access.getAccessExpiresAt() != null
                && LocalDateTime.now().isAfter(access.getAccessExpiresAt())) {
            throw new RuntimeException(
                    "Your access grant for editing this timesheet has expired. "
                            + "Please request access again.");
        }
    }

    /**
     * REQ 8 — After a timesheet is already submitted/locked, additional entries
     * require an approved EXTRA_WORK access request.
     */
    private void validateExtraWorkAccess(Long personId, LocalDate workDate, LocalDate today) {
        if (!workDate.equals(today)) {
            throw new RuntimeException(
                    "Timesheet is locked. Extra work logging is only allowed for today.");
        }

        TimesheetAccessRequest access = accessRequestRepository
                .findByPerson_IdAndRequestedDateAndAccessTypeAndStatus(
                        personId, workDate,
                        TimesheetAccessType.EXTRA_WORK,
                        TimesheetAccessStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException(
                        "Your timesheet for today is already submitted and locked. "
                                + "Please request Admin/HR access to log additional hours."));

        if (access.getAccessExpiresAt() != null
                && LocalDateTime.now().isAfter(access.getAccessExpiresAt())) {
            throw new RuntimeException(
                    "Your extra-work access grant has expired. Please request access again.");
        }
    }

    /**
     * REQ 6 — Timesheet hours must not exceed actual worked hours derived from
     * check-in time minus break durations.
     *
     * Check-in 10:00 → break 11:00–11:15 → current 12:30
     * Gross = 150 min, break = 15 min → actual = 135 min
     * User cannot log 240 min (4 hrs).
     */
    private void validateWorkedHours(int requestedMinutes, int actualWorkedMinutes) {
        if (requestedMinutes > actualWorkedMinutes) {
            throw new RuntimeException(
                    "Timesheet hours (" + TimeUtil.toHHmm(requestedMinutes) +
                            ") cannot exceed actual worked hours (" +
                            TimeUtil.toHHmm(actualWorkedMinutes) + "). " +
                            "Please update your entries to match the time you have actually worked.");
        }
    }

    /**
     * REQ 7 — Buffer rule:
     *  • To log 8 hrs (480 min), employee must have worked ≥ 7 hrs 30 min (450 min).
     *  • To log 4 hrs (240 min), employee must have worked ≥ 3 hrs 30 min (210 min).
     */
    private void validateBufferRule(int requestedMinutes, int actualWorkedMinutes) {
        if (requestedMinutes >= FULL_DAY_MINUTES) {
            int required = FULL_DAY_MINUTES - BUFFER_MINUTES; // 450
            if (actualWorkedMinutes < required) {
                throw new RuntimeException(
                        "You can log 8 hours only after completing at least 7 hours 30 minutes. "
                                + "Actual worked: " + TimeUtil.toHHmm(actualWorkedMinutes));
            }
        } else if (requestedMinutes >= HALF_DAY_MINUTES) {
            int required = HALF_DAY_MINUTES - BUFFER_MINUTES; // 210
            if (actualWorkedMinutes < required) {
                throw new RuntimeException(
                        "You can log 4 hours only after completing at least 3 hours 30 minutes. "
                                + "Actual worked: " + TimeUtil.toHHmm(actualWorkedMinutes));
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PRIVATE — CALCULATION HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Calculates actual worked minutes:
     *   gross = (checkOut or now) − checkIn
     *   actual = gross − sum(all break durations including any ongoing break)
     */
    private int calculateActualWorkedMinutes(Attendance attendance, Long personId) {
        LocalDateTime checkIn     = attendance.getCheckIn();
        LocalDateTime effectiveEnd = attendance.getCheckOut() != null
                ? attendance.getCheckOut()
                : LocalDateTime.now();

        int grossMinutes = (int) Duration.between(checkIn, effectiveEnd).toMinutes();

        List<AttendanceBreakLog> breaks =
                breakLogRepository.findByAttendance_Id(attendance.getId());

        int breakMinutes = 0;
        for (AttendanceBreakLog b : breaks) {
            if (b.getDurationMinutes() != null) {
                breakMinutes += b.getDurationMinutes();
            } else if (b.getBreakEnd() != null) {
                breakMinutes += (int) Duration.between(b.getBreakStart(), b.getBreakEnd()).toMinutes();
            } else {
                // Ongoing break — count from break start to now
                breakMinutes += (int) Duration.between(b.getBreakStart(), LocalDateTime.now()).toMinutes();
            }
        }

        return Math.max(0, grossMinutes - breakMinutes);
    }

    /** Sum all entry worked minutes from the DTO list. */
    private int sumEntryMinutes(List<TimesheetEntryDTO> entries) {
        if (entries == null) return 0;
        return entries.stream()
                .filter(e -> e.getWorkedTime() != null && !e.getWorkedTime().isBlank())
                .mapToInt(e -> TimeUtil.toMinutes(e.getWorkedTime()))
                .sum();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PRIVATE — EMAIL HELPER
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * REQ 4 — Sends submission summary or threaded update email.
     * Stores the returned Message-ID in the Timesheet for future threading.
     */
    private void sendSubmissionEmail(Timesheet timesheet, boolean isUpdate) {

        String personName = timesheet.getPerson().getFirstName()
                + " " + timesheet.getPerson().getLastName();

        String workDate = timesheet.getWorkDate()
                .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH));

        // Build HTML table rows for entries
        StringBuilder rows = new StringBuilder();
        for (TimesheetEntry e : timesheet.getEntries()) {
            rows.append("<tr>")
                    .append("<td>").append(e.getProject().getProjectName()).append("</td>")
                    .append("<td>").append(e.getTaskName() != null ? e.getTaskName() : "—").append("</td>")
                    .append("<td>").append(TimeUtil.toHHmm(e.getWorkedMinutes())).append("</td>")
                    .append("<td>").append(e.getDescription() != null ? e.getDescription() : "—").append("</td>")
                    .append("</tr>");
        }

        String totalTime = TimeUtil.toHHmm(
                timesheet.getTotalMinutes() != null ? timesheet.getTotalMinutes() : 0);

        if (!isUpdate) {
            String msgId = emailService.sendTimesheetSummaryEmail(
                    timesheetSummaryEmail, personName, workDate,
                    rows.toString(), totalTime);

            // Persist Message-ID for threading future updates
            timesheet.setSubmissionEmailMessageId(msgId);
            timesheetRepository.save(timesheet);
        } else {
            emailService.sendTimesheetUpdateEmail(
                    timesheetSummaryEmail, personName, workDate,
                    rows.toString(), totalTime,
                    timesheet.getSubmissionEmailMessageId());
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────

    private Timesheet getOrThrow(Long id) {
        return timesheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));
    }
}