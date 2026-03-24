package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.PersonalInformationRequestDTO;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AddressMapper;
import com.gm.hrms.mapper.PersonalInformationMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.EmployeeBankDetailsService;
import com.gm.hrms.service.LeaveBalanceService;
import com.gm.hrms.service.PersonalInformationService;
import com.gm.hrms.service.WorkProfileService;
import com.gm.hrms.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalInformationServiceImpl implements PersonalInformationService {

    private final PersonalInformationRepository personalInformationRepository;
    private final PersonContactRepository contactRepository;
    private final EmployeeBankDetailsService employeeBankDetailsService;
    private final AddressRepository addressRepository;
    private final WorkProfileService workProfileService;
    private final LeaveBalanceService leaveBalanceService;
    private final WorkProfileRepository workProfileRepository;
    private final LeavePolicyRepository leavePolicyRepository;






    // ================= CREATE =================

    @Override
    public PersonalInformationResponseDTO create(PersonalInformationRequestDTO dto) {

        //  Validate personal email
        if (contactRepository.existsByPersonalEmail(dto.getPersonalEmail())) {
            throw new DuplicateResourceException("Personal email already exists");
        }

        //  Validate office email (only if provided)
        if (dto.getOfficeEmail() != null &&
                contactRepository.existsByOfficeEmail(dto.getOfficeEmail())) {
            throw new DuplicateResourceException("Office email already exists");
        }

        ValidationUtils.validateOfficeEmail(
                dto.getOfficeEmail(),
                dto.getEmploymentType()
        );



        //  Save PersonalInformation
        PersonalInformation personalInformation =
                PersonalInformationMapper.toEntity(dto);

        personalInformation = personalInformationRepository.save(personalInformation);


        if (dto.getBankDetails() != null) {
            employeeBankDetailsService.saveOrUpdate(personalInformation, dto.getBankDetails());
        }

        if (dto.getAddress() != null) {

            Address current = AddressMapper.toEntity(dto.getAddress().getCurrentAddress());

            addressRepository.save(current);

            Address permanent;

            if (Boolean.TRUE.equals(dto.getAddress().getSameAsCurrent())) {
                permanent = current;
            } else {

                permanent = AddressMapper.toEntity(dto.getAddress().getPermanentAddress());

                addressRepository.save(permanent);
            }

            personalInformation.setCurrentAddress(current);
            personalInformation.setPermanentAddress(permanent);
        }

        // 4️⃣ Create Contact
        PersonalInformationContact contact =
                PersonalInformationContact.builder()
                        .personalInformation(personalInformation)
                        .personalPhone(dto.getPersonalPhone())
                        .emergencyPhone(dto.getEmergencyPhone())
                        .personalEmail(dto.getPersonalEmail())
                        .officeEmail(dto.getOfficeEmail()) // use correct field name
                        .build();

        contact = contactRepository.save(contact);

        if (dto.getWorkProfile() == null)
            throw new InvalidRequestException("Work Profile data required");
        workProfileService.create(personalInformation.getId(),dto.getWorkProfile());


        //  IMPORTANT: Set inverse side to avoid NPE
        personalInformation.setContact(contact);


        Long policyId = getPolicyId(personalInformation.getId());

        leaveBalanceService.initializeLeaveBalance(personalInformation.getId(), policyId);




        return PersonalInformationMapper.toResponse(personalInformation);
    }// ================= UPDATE =================

    @Override
    @Transactional
    public PersonalInformationResponseDTO update(Long id,
                                                 PersonalInformationRequestDTO dto) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Personal information not found"));

        //  PATCH MAIN ENTITY USING MAPPER
        PersonalInformationMapper.updateEntity(personalInformation, dto);

        // ===== CONTACT PATCH =====
        PersonalInformationContact contact = personalInformation.getContact();

        if (contact == null) {
            contact = new PersonalInformationContact();
            contact.setPersonalInformation(personalInformation);
        }

        WorkProfile workProfile = personalInformation.getWorkProfile();
        if (workProfile != null) {
            workProfileService.update(personalInformation.getWorkProfile().getId(),dto.getWorkProfile());
        }


        //  Duplicate check ONLY if email is provided and changed
        if (dto.getPersonalEmail() != null &&
                (contact.getPersonalEmail() == null ||
                        !contact.getPersonalEmail().equals(dto.getPersonalEmail()))) {

            if (contactRepository.existsByPersonalEmail(dto.getPersonalEmail())) {
                throw new DuplicateResourceException("Personal email already exists");
            }
        }
        if (dto.getOfficeEmail() != null &&
                (contact.getOfficeEmail() == null ||
                        !contact.getOfficeEmail().equals(dto.getOfficeEmail()))) {

            if (contactRepository.existsByPersonalEmail(dto.getOfficeEmail())) {
                throw new DuplicateResourceException("Office email already exists");
            }
        }

        if (dto.getBankDetails() != null) {
            employeeBankDetailsService.saveOrUpdate(personalInformation, dto.getBankDetails());
        }

        if (dto.getAddress() != null) {

            if (dto.getAddress().getCurrentAddress() != null) {

                Address current = personalInformation.getCurrentAddress();

                if (current == null) {

                    current = AddressMapper.toEntity(dto.getAddress().getCurrentAddress());
                    addressRepository.save(current);

                } else {

                    AddressMapper.patchEntity(current, dto.getAddress().getCurrentAddress());
                }

                personalInformation.setCurrentAddress(current);
            }

            if (Boolean.TRUE.equals(dto.getAddress().getSameAsCurrent())) {

                personalInformation.setPermanentAddress(personalInformation.getCurrentAddress());

            } else if (dto.getAddress().getPermanentAddress() != null) {

                Address permanent = personalInformation.getPermanentAddress();

                if (permanent == null) {

                    permanent = AddressMapper.toEntity(dto.getAddress().getPermanentAddress());
                    addressRepository.save(permanent);

                } else {

                    AddressMapper.patchEntity(permanent, dto.getAddress().getPermanentAddress());
                }

                personalInformation.setPermanentAddress(permanent);
            }
        }

        //  PATCH CONTACT USING MAPPER
        PersonalInformationMapper.updateContact(contact, dto);

        contactRepository.save(contact);

        return PersonalInformationMapper.toResponse(personalInformation);
    }
    // ================= GET BY ID =================

    @Override
    @Transactional(readOnly = true)
    public PersonalInformationResponseDTO getById(Long id) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation not found"));

        return PersonalInformationMapper.toResponse(personalInformation);
    }

    // ================= GET ALL =================

    @Override
    @Transactional(readOnly = true)
    public List<PersonalInformationResponseDTO> getAll() {

        return personalInformationRepository.findAll()
                .stream()
                .map(PersonalInformationMapper::toResponse)
                .toList();
    }

    // ================= DELETE (SOFT DELETE) =================

    @Override
    public void delete(Long id) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation not found"));

        personalInformation.setActive(false);
    }

    // 🔥 FINAL POLICY RESOLUTION (ENTERPRISE)
    private Long getPolicyId(Long personalId) {

        WorkProfile profile = workProfileRepository
                .findByPersonalInformationId(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Work profile not found"));

        EmploymentType type = profile.getPersonalInformation().getEmploymentType();

        LeavePolicy policy = leavePolicyRepository
                .findByEmploymentTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new ResourceNotFoundException("Leave policy not found"));

        return policy.getId();
    }
}