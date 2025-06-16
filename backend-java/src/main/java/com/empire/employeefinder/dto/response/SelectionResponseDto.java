package com.empire.employeefinder.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectionResponseDto {

    private Long id;

    private String companyName;

    private String regNumber;

    private List<EmployeeResponseDto> candidates;
}
