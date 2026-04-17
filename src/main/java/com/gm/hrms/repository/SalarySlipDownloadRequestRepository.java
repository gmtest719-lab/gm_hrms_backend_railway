package com.gm.hrms.repository;

import com.gm.hrms.entity.SalarySlipDownloadRequest;
import com.gm.hrms.enums.DownloadRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalarySlipDownloadRequestRepository
        extends JpaRepository<SalarySlipDownloadRequest, Long> {

    /**
     * Returns the latest active (PENDING or APPROVED) request for a given slip.
     * Used to prevent duplicate requests.
     */
    @Query("""
        SELECT r FROM SalarySlipDownloadRequest r
        WHERE r.salarySlip.id = :slipId
          AND r.status IN (
              com.gm.hrms.enums.DownloadRequestStatus.PENDING,
              com.gm.hrms.enums.DownloadRequestStatus.APPROVED
          )
        ORDER BY r.requestedAt DESC
        """)
    Optional<SalarySlipDownloadRequest> findActiveBySlipId(@Param("slipId") Long slipId);

    /** Admin: all requests with a given status. */
    List<SalarySlipDownloadRequest> findByStatusOrderByRequestedAtAsc(DownloadRequestStatus status);

    /** Admin: all requests regardless of status (full history). */
    List<SalarySlipDownloadRequest> findAllByOrderByRequestedAtDesc();

    /** Employee: history for a personal profile. */
    List<SalarySlipDownloadRequest> findByPersonalInformationIdOrderByRequestedAtDesc(
            Long personalId);

    /** Fetch the approved request for a slip — used during password-verify step. */
    Optional<SalarySlipDownloadRequest> findBySalarySlipIdAndStatus(
            Long slipId, DownloadRequestStatus status);
}