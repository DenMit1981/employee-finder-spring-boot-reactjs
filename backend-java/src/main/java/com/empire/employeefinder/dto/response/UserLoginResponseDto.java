package com.empire.employeefinder.dto.response;

import com.empire.employeefinder.model.enums.JobTitle;
import com.empire.employeefinder.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginResponseDto {

    private String username;

    private String email;

    private JobTitle jobTitle;

    private String companyName;

    private String regNumber;

    private String token;

    private Role role;
}
