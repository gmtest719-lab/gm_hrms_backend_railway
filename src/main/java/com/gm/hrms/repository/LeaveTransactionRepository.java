package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveTransactionRepository extends JpaRepository<LeaveTransaction, Long> {

    List<LeaveTransaction> findByLeaveBalanceId(Long leaveBalanceId);

    List<LeaveTransaction> findByLeaveBalancePersonalId(Long personalId);
}