package com.empire.employeefinder.dto.response;

import com.empire.employeefinder.model.enums.JobTitle;
import com.empire.employeefinder.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private Long id;

    private String name;

    private String email;

    private String companyName;

    private String regNumber;

    private JobTitle jobTitle;

    private Role role;
}
