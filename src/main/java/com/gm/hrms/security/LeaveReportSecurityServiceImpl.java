package com.gm.hrms.security;

import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.InternRepository;
import com.gm.hrms.repository.TraineeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveReportSecurityServiceImpl implements LeaveReportSecurityService {

    // ──────────────────────────────────────────────────────────────
    // Repositories used to map username → personalId.
    // Adjust this if your auth principal already carries personalId.
    // ──────────────────────────────────────────────────────────────
    private final EmployeeRepository employeeRepository;
    private final InternRepository   internRepository;
    private final TraineeRepository  traineeRepository;

    // ── role check ────────────────────────────────────────────────
    @Override
    public boolean isAdminOrHr() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                        a.getAuthority().equals("ROLE_HR"));
    }

    // ── personalId enforcement ─────────────────────────────────────
    @Override
    public Long enforcePersonalId(Long requestedId) {

        if (isAdminOrHr()) {
            return requestedId; // null = no restriction, specific value = filter by that ID
        }

        Long currentId = resolveCurrentPersonalId();

        if (requestedId != null && !requestedId.equals(currentId)) {
            throw new AccessDeniedException("You are not allowed to view another employee's data");
        }

        return currentId; // always restrict to self
    }

    // ── filter sanitisation for restricted roles ──────────────────
    @Override
    public void sanitizeFilterForRole(LeaveReportFilterDTO filter) {
        if (!isAdminOrHr()) {
            // Department / designation scoping is ADMIN/HR-only
            filter.setDepartmentId(null);
            filter.setDesignationId(null);
            filter.setApproverId(null);
        }
    }

    // ── resolve current user's personalId ─────────────────────────
    // Looks up by employee/intern/trainee code stored as the
    // authentication principal name (e.g. "GM001").
    // ──────────────────────────────────────────────────────────────
    private Long resolveCurrentPersonalId() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Optional<Employee> emp = employeeRepository.findByEmployeeCode(username);
        if (emp.isPresent()) {
            return emp.get().getPersonalInformation().getId();
        }

        Optional<Intern> intern = internRepository.findByInternCode(username);
        if (intern.isPresent()) {
            return intern.get().getPersonalInformation().getId();
        }

        Optional<Trainee> trainee = traineeRepository.findByTraineeCode(username);
        if (trainee.isPresent()) {
            return trainee.get().getPersonalInformation().getId();
        }

        throw new ResourceNotFoundException("Current user not found: " + username);
    }
}