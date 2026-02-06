package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.response.EmployeeAddressResponseDTO;
import com.gm.hrms.dto.response.EmployeeContactResponseDTO;
import com.gm.hrms.dto.response.EmployeeDocumentResponseDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.entity.*;

import java.util.List;

public class EmployeeMapper {

    public static Employee toEntity(EmployeeRequestDTO dto,
                                    Department dept,
                                    Designation desig){

        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .employeeCode(dto.getEmployeeCode())
                .dateOfJoining(dto.getDateOfJoining())
                .yearOfExperience(dto.getYearOfExperience())
                .employmentType(dto.getEmploymentType())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .profileImageUrl(dto.getProfileImageUrl())
                .department(dept)
                .designation(desig)
                .role(dto.getRole())
                .build();
    }

    public static EmployeeResponseDTO toResponse(Employee e){

        return EmployeeResponseDTO.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .employeeCode(e.getEmployeeCode())

                .departmentName(e.getDepartment().getName())
                .designationName(e.getDesignation().getName())

                .active(e.getActive())
                .role(e.getRole())

                .contact(mapContact(e.getContact()))
                .address(mapAddress(e.getAddress()))
                .documents(mapDocuments(e.getDocuments()))

                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private static EmployeeContactResponseDTO mapContact(EmployeeContact contact){

        if(contact == null) return null;

        return EmployeeContactResponseDTO.builder()
                .personalEmail(contact.getPersonalEmail())
                .officeEmail(contact.getOfficeEmail())
                .personalPhone(contact.getPersonalPhone())
                .emergencyPhone(contact.getEmergencyPhone())
                .build();
    }

    private static EmployeeAddressResponseDTO mapAddress(EmployeeAddress address){

        if(address == null) return null;

        return EmployeeAddressResponseDTO.builder()
                .currentAddress(address.getCurrentAddress())
                .permanentAddress(address.getPermanentAddress())
                .build();
    }


    private static List<EmployeeDocumentResponseDTO> mapDocuments(List<EmployeeDocument> docs){

        if(docs == null) return List.of();

        return docs.stream()
                .map(d -> EmployeeDocumentResponseDTO.builder()
                        .id(d.getId())
                        .documentType(d.getDocumentType())
                        .filePath(d.getFilePath())
                        .build())
                .toList();
    }




}
