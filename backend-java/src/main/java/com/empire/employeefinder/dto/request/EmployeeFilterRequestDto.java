package com.empire.employeefinder.dto.request;

import com.empire.employeefinder.model.enums.*;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Setter
public class EmployeeFilterRequestDto {

    private String name;

    private Gender gender;

    private Location location;

    private Long jobTypeId;

    private Long jobPositionId;

    private Long experienceYears;

    private BigDecimal expectedSalary;

    private LocalDate availabilityDate;

    private HighestEducationLevel educationLevel;

    private Long age;
}


