package com.gm.hrms.service;

import com.gm.hrms.entity.LeaveBalance;
import com.gm.hrms.enums.LeaveTransactionType;

public interface LeaveTransactionService {

    void log(
            LeaveBalance balance,
            LeaveTransactionType type,
            int days,
            int before,
            int after,
            Long referenceId,
            String remarks
    );
}