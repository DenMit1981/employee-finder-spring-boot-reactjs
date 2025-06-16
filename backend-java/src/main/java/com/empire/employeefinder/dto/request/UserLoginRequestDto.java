package com.empire.employeefinder.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class UserLoginRequestDto {

    private static final String LOGIN = "Enter email";
    private static final String PASSWORD = "Enter password";
    private static final String WRONG_SIZE_OF_NAME_OR_PASSWORD = "Login or password shouldn't be less than 4 symbols";
    private static final String INVALID_LOGIN_OR_PASSWORD = "Login or password should contains Latin letters";
    private static final String INVALID_NUMBER_OF_SYMBOLS_FOR_EMAIL = "Email shouldn't be less than 6 symbols";
    private static final String INVALID_EMAIL = "Email should contain symbol @";

    @NotBlank(message = LOGIN)
    @Size(min = 6, message = INVALID_NUMBER_OF_SYMBOLS_FOR_EMAIL)
    @Email(regexp = "^[^@|\\.].+@.+\\..+[^@|\\.]$", message = INVALID_EMAIL)
    private String login;

    @NotBlank(message = PASSWORD)
    @Size(min = 4, message = WRONG_SIZE_OF_NAME_OR_PASSWORD)
    @Pattern(regexp = "^[^А-Яа-я]*$", message = INVALID_LOGIN_OR_PASSWORD)
    private String password;
}
