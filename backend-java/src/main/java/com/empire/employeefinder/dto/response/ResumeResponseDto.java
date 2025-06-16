package com.empire.employeefinder.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResumeResponseDto {

    private Long id;

    private String fileName;

    private String fileSize;
}
