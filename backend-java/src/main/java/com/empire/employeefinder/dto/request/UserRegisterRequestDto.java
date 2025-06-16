package com.empire.employeefinder.dto.request;

import com.empire.employeefinder.model.enums.JobTitle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class UserRegisterRequestDto {

    private static final String NAME = "Enter name";
    private static final String COMPANY_NAME = "Enter company name";
    private static final String PASSWORD = "Enter password";
    private static final String CONFIRM_PASSWORD = "Confirm password";
    private static final String EMAIL = "Enter email";
    private static final String WRONG_SIZE_OF_COMPANY_NAME = "Company name shouldn't be less than 2 symbols";
    private static final String WRONG_SIZE_OF_NAME_OR_PASSWORD = "Login or password shouldn't be less than 4 symbols";
    private static final String WRONG_SIZE_OF_REG_NUMBER = "Registration number shouldn't be less than 8 symbols";
    private static final String INVALID_LOGIN_OR_PASSWORD = "Login or password should contains Latin letters";
    private static final String INVALID_NUMBER_OF_SYMBOLS_FOR_EMAIL = "Email shouldn't be less than 6 symbols";
    private static final String INVALID_EMAIL = "Email should contain symbol @";

    @NotBlank(message = NAME)
    @Size(min = 4, message = WRONG_SIZE_OF_NAME_OR_PASSWORD)
    @Pattern(regexp = "^[^А-Яа-я]*$", message = INVALID_LOGIN_OR_PASSWORD)
    private String name;

    @Valid
    private CompanyRequestDto company;

    private JobTitle jobTitle;

    @NotBlank(message = PASSWORD)
    @Size(min = 4, message = WRONG_SIZE_OF_NAME_OR_PASSWORD)
    @Pattern(regexp = "^[^А-Яа-я]*$", message = INVALID_LOGIN_OR_PASSWORD)
    private String password;

    @NotBlank(message = CONFIRM_PASSWORD)
    @Size(min = 4, message = WRONG_SIZE_OF_NAME_OR_PASSWORD)
    @Pattern(regexp = "^[^А-Яа-я]*$", message = INVALID_LOGIN_OR_PASSWORD)
    private String confirmPassword;

    @NotBlank(message = EMAIL)
    @Size(min = 6, message = INVALID_NUMBER_OF_SYMBOLS_FOR_EMAIL)
    @Email(regexp = "^[^@|\\.].+@.+\\..+[^@|\\.]$", message = INVALID_EMAIL)
    private String email;
}
