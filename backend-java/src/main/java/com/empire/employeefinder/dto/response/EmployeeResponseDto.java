package com.empire.employeefinder.dto.response;

import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.HighestEducationLevel;
import com.empire.employeefinder.model.enums.Location;
import com.empire.employeefinder.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponseDto {

    private Long id;

    private String name;

    private Gender gender;

    private Location location;

    private String jobType;

    private String jobPosition;

    private Long experienceYears;

    private BigDecimal expectedSalary;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate availabilityDate;

    private HighestEducationLevel educationLevel;

    private Long age;

    private Status status;

    private ResumeResponseDto resume;
}
