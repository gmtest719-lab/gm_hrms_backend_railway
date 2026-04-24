package com.gm.hrms.enums;

public enum TimesheetAccessType {
    EDIT_OLD,    // Request to edit a timesheet older than 3 days
    EXTRA_WORK   // Request to log additional hours after today's timesheet is already submitted
}