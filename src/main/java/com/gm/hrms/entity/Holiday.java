package com.gm.hrms.entity;

import com.gm.hrms.enums.HolidayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "holidays")
@Getter
@Setter
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String holidayName;

    private LocalDate holidayDate;

    @Enumerated(EnumType.STRING)
    private HolidayType holidayType;

    private String description;

    private Boolean isOptional;

    private Boolean isActive;

    private LocalDateTime createdAt;
}