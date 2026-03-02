package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;

public class EmployeeMapper {

    private EmployeeMapper() {}

    // ================= CREATE =================

    public static Employee toEntity(EmployeeRequestDTO dto,
                                    Department dept,
                                    Designation desig,
                                    Employee reportingManager,
                                    String autoCode) {

        return Employee.builder()
                .employeeCode(autoCode)
                .department(dept)
                .designation(desig)
                .role(dto.getRole())
                .reportingManager(reportingManager)
                .build();
    }

    // ================= RESPONSE =================

    public static EmployeeResponseDTO toResponse(Employee e) {

        PersonalInformation p = e.getPersonalInformation();
        PersonalInformationContact contact =
                p != null ? p.getContact() : null;
        EmployeeAddress address =
                p != null ? p.getAddress() : null;

        BankLegalDetails bankLegalDetails =

        p != null ? p.getBankLegalDetails() : null;
        return EmployeeResponseDTO.builder()

                // ===== IDS =====
                .personalInformationId(p != null ? p.getId() : null)
                .employeeId(e.getId())

                // ===== PERSONAL =====
                .firstName(p != null ? p.getFirstName() : null)
                .middleName(p != null ? p.getMiddleName() : null)
                .lastName(p != null ? p.getLastName() : null)
                .gender(p != null ? p.getGender() : null)
                .dateOfBirth(p != null ? p.getDateOfBirth() : null)
                .maritalStatus(p != null ? p.getMaritalStatus() : null)
                .spouseOrParentName(p != null ? p.getSpouseOrParentName() : null)
                .active(p != null ? p.getActive() : null)

                // ===== EMPLOYEE CORE =====
                .employeeCode(e.getEmployeeCode())
                .departmentName(
                        e.getDepartment() != null ? e.getDepartment().getName() : null
                )
                .designationName(
                        e.getDesignation() != null ? e.getDesignation().getName() : null
                )
                .reportingManagerName(
                        e.getReportingManager() != null
                                ? e.getReportingManager().getEmployeeCode()
                                : null
                )
                .role(e.getRole())

                // ===== CONTACT =====
                .contact(mapContact(contact))

                // ===== MODULES =====
                .employment(mapEmployment(e.getEmployment()))
                .bankDetails(mapBank(bankLegalDetails))
                .address(mapAddress(address))

                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    // ================= CONTACT =================

     static EmployeeContactResponseDTO mapContact(
            PersonalInformationContact contact) {

        if (contact == null) return null;

        return EmployeeContactResponseDTO.builder()
                .personalPhone(contact.getPersonalPhone())
                .emergencyPhone(contact.getEmergencyPhone())
                .personalEmail(contact.getPersonalEmail())
                .officeEmail(contact.getOfficeEmail())
                .build();
    }

    // ================= EMPLOYMENT =================

    private static EmployeeEmploymentResponseDTO mapEmployment(EmployeeEmployment emp) {

        if (emp == null) return null;

        return EmployeeEmploymentResponseDTO.builder()
                .dateOfJoining(emp.getDateOfJoining())
                .yearOfExperience(emp.getYearOfExperience())
                .ctc(emp.getCtc())
                .workMode(emp.getWorkMode())
                .employeeStatus(emp.getEmployeeStatus())
                .noticePeriod(emp.getNoticePeriod())
                .shiftTiming(emp.getShiftTiming())
                .branchName(emp.getBranchName())
                .build();
    }

    // ================= BANK =================

     static EmployeeBankDetailsResponseDTO mapBank(BankLegalDetails bank) {

        if (bank == null) return null;

        return EmployeeBankDetailsResponseDTO.builder()
                .bankName(bank.getBankName())
                .accountNumber(bank.getAccountNumber())
                .ifscCode(bank.getIfscCode())
                .panNumber(bank.getPanNumber())
                .aadhaarNumber(bank.getAadhaarNumber())
                .uanNumber(bank.getUanNumber())
                .esicNumber(bank.getEsicNumber())
                .build();
    }

    // ================= ADDRESS =================

    private static EmployeeAddressResponseDTO mapAddress(EmployeeAddress address) {

        if (address == null) return null;

        return EmployeeAddressMapper.toResponse(address);
    }
}