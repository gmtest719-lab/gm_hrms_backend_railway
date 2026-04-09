package com.gm.hrms.specification;

import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import com.gm.hrms.entity.LeaveRequest;
import com.gm.hrms.entity.WorkProfile;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LeaveRequestReportSpecification {

    private LeaveRequestReportSpecification() {}

    public static Specification<LeaveRequest> filter(
            LeaveReportFilterDTO filter,
            Long personalId) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ── Personal ID (row-level security)
            if (personalId != null) {
                predicates.add(cb.equal(root.get("personalId"), personalId));
            }

            // ── Date range
            if (filter.getFromDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("startDate"), filter.getFromDate()));
            }
            if (filter.getToDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("endDate"), filter.getToDate()));
            }

            // ── Leave type
            if (filter.getLeaveTypeId() != null) {
                predicates.add(
                        cb.equal(root.get("leaveType").get("id"), filter.getLeaveTypeId()));
            }

            // ── Status
            if (filter.getStatus() != null) {
                predicates.add(
                        cb.equal(root.get("status"), filter.getStatus()));
            }

            // ── Approver (approvedBy is a Long)
            if (filter.getApproverId() != null) {
                predicates.add(
                        cb.equal(root.get("approvedBy"), filter.getApproverId()));
            }

            // ── Department — subquery through WorkProfile ─────────
            if (filter.getDepartmentId() != null) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<WorkProfile> wp = sub.from(WorkProfile.class);
                sub.select(wp.get("personalInformation").get("id"))
                        .where(cb.equal(
                                wp.get("department").get("id"),
                                filter.getDepartmentId()));
                predicates.add(root.get("personalId").in(sub));
            }

            // ── Designation — same subquery pattern
            if (filter.getDesignationId() != null) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<WorkProfile> wp = sub.from(WorkProfile.class);
                sub.select(wp.get("personalInformation").get("id"))
                        .where(cb.equal(
                                wp.get("designation").get("id"),
                                filter.getDesignationId()));
                predicates.add(root.get("personalId").in(sub));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}