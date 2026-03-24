package com.gm.hrms.util;

import com.gm.hrms.dto.request.EmployeeBankDetailsRequestDTO;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.exception.InvalidRequestException;

public class ValidationUtils {

    private ValidationUtils() {}

    // =====================================================
    // ================= STRING =============================
    // =====================================================

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static void requireNonBlank(String value, String message) {
        if (isBlank(value)) {
            throw new InvalidRequestException(message);
        }
    }

    // =====================================================
    // ================= EMAIL ==============================
    // =====================================================

    public static void validateOfficeEmail(String email, EmploymentType type) {

        boolean isIntern = type == EmploymentType.INTERN;

        if (!isIntern && isBlank(email)) {
            throw new InvalidRequestException("Office email is required");
        }
    }

    // =====================================================
    // ================= BANK ===============================
    // =====================================================

    public static void validateBankIfRequired(
            PersonalInformation person,
            EmployeeBankDetailsRequestDTO dto,
            Intern intern
    ) {

        if (person == null) return;

        EmploymentType type = person.getEmploymentType();

        boolean isMandatory = false;

        // EMPLOYEE
        if (type == EmploymentType.EMPLOYEE) {
            isMandatory = true;
        }

        // TRAINEE
        if (type == EmploymentType.TRAINEE) {
            isMandatory = true;
        }

        // INTERN (PAID)
        if (type == EmploymentType.INTERN &&
                intern != null &&
                intern.getInternshipDetails() != null &&
                intern.getInternshipDetails().getInternshipType() == InternShipType.PAID) {

            isMandatory = true;
        }

        // FINAL CHECK
        if (isMandatory) {

            if (dto == null ||
                    isBlank(dto.getBankName()) ||
                    isBlank(dto.getAccountNumber()) ||
                    isBlank(dto.getIfscCode())) {

                throw new InvalidRequestException(
                        "Bank details (Bank Name, Account Number, IFSC) are mandatory"
                );
            }
        }
    }

    // =====================================================
    // ================= GENERIC ============================
    // =====================================================

    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new InvalidRequestException(message);
        }
    }
}