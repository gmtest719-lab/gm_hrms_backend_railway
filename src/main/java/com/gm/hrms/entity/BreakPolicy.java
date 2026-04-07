package com.gm.hrms.entity;

import com.gm.hrms.enums.BreakCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "break_policies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakPolicy extends  BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String breakName;

    @Enumerated(EnumType.STRING)
    private BreakCategory breakCategory;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    private Integer breakDurationMinutes;

    private Boolean isPaid;

    private Boolean isActive;

}
