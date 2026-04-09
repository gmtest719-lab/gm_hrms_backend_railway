package com.gm.hrms.security;

public interface LeaveReportSecurityService {

    boolean isAdminOrHr();

    Long enforcePersonalId(Long requestedId);

    void sanitizeFilterForRole(com.gm.hrms.dto.request.LeaveReportFilterDTO filter);
}