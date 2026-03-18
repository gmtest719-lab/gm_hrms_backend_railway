package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeBankDetailsRequestDTO;
import com.gm.hrms.entity.BankLegalDetails;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.repository.BankLegalDetailsRepository;
import com.gm.hrms.repository.InternRepository;
import com.gm.hrms.service.EmployeeBankDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@RequiredArgsConstructor
public class EmployeeBankDetailsServiceImpl
        implements EmployeeBankDetailsService {

    private final InternRepository internRepository;
    private final BankLegalDetailsRepository repository;

    @Override
    public void saveOrUpdate(
            PersonalInformation personalInformation,
            EmployeeBankDetailsRequestDTO dto
    ) {

        // ================= VALIDATION =================

        validateBankIfRequired(personalInformation, dto);

        // ================= SAVE =================

        BankLegalDetails bank =
                repository.findByPersonalInformation(personalInformation)
                        .orElse(new BankLegalDetails());

        bank.setPersonalInformation(personalInformation);

        if (dto != null) {

            if (dto.getBankName() != null)
                bank.setBankName(dto.getBankName());

            if (dto.getAccountNumber() != null)
                bank.setAccountNumber(dto.getAccountNumber());

            if (dto.getIfscCode() != null)
                bank.setIfscCode(dto.getIfscCode());

            if (dto.getPanNumber() != null)
                bank.setPanNumber(dto.getPanNumber());

            if (dto.getAadhaarNumber() != null)
                bank.setAadhaarNumber(dto.getAadhaarNumber());

            if (dto.getUanNumber() != null)
                bank.setUanNumber(dto.getUanNumber());

            if (dto.getEsicNumber() != null)
                bank.setEsicNumber(dto.getEsicNumber());

            if (dto.getPfNumber() != null)
                bank.setPfNumber(dto.getPfNumber());
        }

        repository.save(bank);
    }
    private void validateBankIfRequired(
            PersonalInformation person,
            EmployeeBankDetailsRequestDTO dto
    ) {

        var type = person.getEmploymentType();

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
        if (type == EmploymentType.INTERN) {

            Intern intern = internRepository
                    .findByPersonalInformation(person)
                    .orElse(null);

            if (intern != null &&
                    intern.getInternshipDetails() != null &&
                    intern.getInternshipDetails().getInternshipType() == InternShipType.PAID) {

                isMandatory = true;
            }
        }

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
}