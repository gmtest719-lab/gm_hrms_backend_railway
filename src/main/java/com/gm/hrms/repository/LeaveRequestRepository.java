package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveRequest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Long>,
        JpaSpecificationExecutor<LeaveRequest> {

    Page<LeaveRequest> findByPersonalId(Long personalId, Pageable pageable);

    boolean existsByPersonalIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long personalId, LocalDate end, LocalDate start);

    @Query("""
        SELECT COUNT(lr) FROM LeaveRequest lr
        JOIN lr.leaveType lt
        WHERE lr.personalId = :personalId
          AND lr.status     = 'APPROVED'
          AND lt.isPaid     = true
          AND lr.startDate >= :from AND lr.endDate <= :to
          AND (lr.isCancelled IS NULL OR lr.isCancelled = false)
        """)
    long countApprovedPaidLeaves(
            @Param("personalId") Long personalId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("""
        SELECT COALESCE(SUM(lr.totalDays), 0.0) FROM LeaveRequest lr
        JOIN lr.leaveType lt
        WHERE lr.personalId = :personalId
          AND lr.status     = 'APPROVED'
          AND lt.isPaid     = false
          AND lr.startDate >= :from AND lr.endDate <= :to
          AND (lr.isCancelled IS NULL OR lr.isCancelled = false)
        """)
    double sumUnpaidApprovedLeaveDays(
            @Param("personalId") Long personalId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.status IN ('APPROVED','REJECTED')
          AND (:approverId IS NULL OR lr.approvedBy = :approverId)
          AND (:fromDate   IS NULL OR lr.startDate >= :fromDate)
          AND (:toDate     IS NULL OR lr.endDate   <= :toDate)
        """)
    Page<LeaveRequest> findApprovalRecords(
            @Param("approverId") Long approverId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);
}