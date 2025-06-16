package com.empire.employeefinder.dto.request;

import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.HighestEducationLevel;
import com.empire.employeefinder.model.enums.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Setter
public class EmployeeRequestDto {

    private static final String NAME = "Enter employee name";
    private static final String JOB_TYPE = "Enter job type";
    private static final String JOB_POSITION = "Enter job position";
    private static final String EXPERIENCE_YEARS = "Enter experience years";
    private static final String EXPECTED_SALARY = "Enter expected salary";
    private static final String AGE = "Enter age";

    @NotBlank(message = NAME)
    private String name;

    private Gender gender;

    private Location location;

    @NotNull(message = JOB_TYPE)
    private Long jobTypeId;

    @NotNull(message = JOB_POSITION)
    private Long jobPositionId;

    @NotNull(message = EXPERIENCE_YEARS)
    private Long experienceYears;

    @NotNull(message = EXPECTED_SALARY)
    private BigDecimal expectedSalary;

    private LocalDate availabilityDate;

    private HighestEducationLevel educationLevel;

    @NotNull(message = AGE)
    private Long age;

    private MultipartFile resume;
}
