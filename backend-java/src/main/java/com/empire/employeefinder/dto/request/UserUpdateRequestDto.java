package com.empire.employeefinder.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class UserUpdateRequestDto {

    private static final String LOGIN = "Enter email";
    private static final String INVALID_NUMBER_OF_SYMBOLS_FOR_EMAIL = "Email shouldn't be less than 6 symbols";
    private static final String INVALID_EMAIL = "Email should contain symbol @";
    private static final String CURRENT_PASSWORD = "Enter current password";
    private static final String NEW_PASSWORD = "Enter new password";
    private static final String CONFIRM_PASSWORD = "Confirm new password";
    private static final String WRONG_SIZE_OF_PASSWORD = "Password shouldn't be less than 4 symbols";
    private static final String INVALID_PASSWORD = "Password should contains Latin letters";

    @NotBlank(message = LOGIN)
    @Size(min = 6, message = INVALID_NUMBER_OF_SYMBOLS_FOR_EMAIL)
    @Email(regexp = "^[^@|\\.].+@.+\\..+[^@|\\.]$", message = INVALID_EMAIL)
    private String login;

    @NotBlank(message = CURRENT_PASSWORD)
    @Size(min = 4, message = WRONG_SIZE_OF_PASSWORD)
    @Pattern(regexp = "^[^А-Яа-я]*$", message = INVALID_PASSWORD)
    private String currentPassword;

    @NotBlank(message = NEW_PASSWORD)
    @Size(min = 4, message = WRONG_SIZE_OF_PASSWORD)
    @Pattern(regexp = "^[^А-Яа-я]*$", message = INVALID_PASSWORD)
    private String newPassword;

    @NotBlank(message = CONFIRM_PASSWORD)
    @Size(min = 4, message = WRONG_SIZE_OF_PASSWORD)
    @Pattern(regexp = "^[^А-Яа-я]*$", message = INVALID_PASSWORD)
    private String confirmPassword;
}
