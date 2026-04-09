package com.gm.hrms.specification;

import com.gm.hrms.dto.request.LeaveReportFilterDTO;
import com.gm.hrms.entity.LeaveBalance;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LeaveBalanceReportSpecification {

    private LeaveBalanceReportSpecification() {}

    public static Specification<LeaveBalance> filter(
            LeaveReportFilterDTO filter,
            Long personalId) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (personalId != null) {
                predicates.add(cb.equal(root.get("personal").get("id"), personalId));
            }

            if (filter.getLeaveTypeId() != null) {
                predicates.add(cb.equal(root.get("leaveType").get("id"), filter.getLeaveTypeId()));
            }

            if (filter.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), filter.getYear()));
            }

            // Department — direct join via personal → workProfile
            if (filter.getDepartmentId() != null) {
                Join<?, ?> personal   = root.join("personal",   JoinType.INNER);
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