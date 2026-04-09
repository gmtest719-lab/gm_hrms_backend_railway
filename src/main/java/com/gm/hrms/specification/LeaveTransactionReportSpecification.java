package com.gm.hrms.specification;

import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import com.gm.hrms.entity.LeaveTransaction;
import com.gm.hrms.enums.LeaveTransactionType;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LeaveTransactionReportSpecification {

    private LeaveTransactionReportSpecification() {}


    public static Specification<LeaveTransaction> encashmentFilter(
            LeaveReportFilterDTO filter,
            Long personalId) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Always restrict to encashment transactions
            predicates.add(cb.equal(root.get("type"), LeaveTransactionType.ENCASHMENT));

            if (personalId != null) {
                predicates.add(cb.equal(root.get("personal").get("id"), personalId));
            }

            if (filter.getLeaveTypeId() != null) {
                predicates.add(cb.equal(
                        root.get("leaveBalance").get("leaveType").get("id"),
                        filter.getLeaveTypeId()));
            }

            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("transactionDate"),
                        filter.getFromDate().atStartOfDay()));
            }

            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("transactionDate"),
                        filter.getToDate().atTime(23, 59, 59)));
            }

            // Department
            if (filter.getDepartmentId() != null) {
                Join<?, ?> personal    = root.join("personal",    JoinType.INNER);
                Join<?, ?> workProfile = personal.join("workProfile", JoinType.INNER);
                predicates.add(cb.equal(
                        workProfile.get("department").get("id"),
                        filter.getDepartmentId()));
            }

            if (filter.getDesignationId() != null) {
                Join<?, ?> personal    = root.join("personal",    JoinType.LEFT);
                Join<?, ?> workProfile = personal.join("workProfile", JoinType.LEFT);
                predicates.add(cb.equal(
                        workProfile.get("designation").get("id"),
                        filter.getDesignationId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}