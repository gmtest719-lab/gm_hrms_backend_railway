package com.gm.hrms.dto.response;

import com.gm.hrms.enums.Gender;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeFilterOptionsDTO {

    private List<FilterOptionDTO> departments;
    private List<FilterOptionDTO> designations;
    private List<FilterOptionDTO> branches;
    private List<FilterOptionDTO> employees;
    private List<Boolean>         activeOptions;
    private List<Gender>          genders;
}