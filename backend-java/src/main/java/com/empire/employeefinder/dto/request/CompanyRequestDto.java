package com.empire.employeefinder.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class CompanyRequestDto {

    private static final String COMPANY_NAME = "Enter company name";
    private static final String REG_NUMBER = "Enter reg number";
    private static final String WRONG_SIZE_OF_COMPANY_NAME = "Company name shouldn't be less than 2 symbols";
    private static final String WRONG_SIZE_OF_REG_NUMBER = "Registration number shouldn't be less than 8 symbols";

    @NotBlank(message = COMPANY_NAME)
    @Size(min = 2, message = WRONG_SIZE_OF_COMPANY_NAME)
    private String companyName;

    @NotBlank(message = REG_NUMBER)
    @Size(min = 8, message = WRONG_SIZE_OF_REG_NUMBER)
    private String regNumber;
}
