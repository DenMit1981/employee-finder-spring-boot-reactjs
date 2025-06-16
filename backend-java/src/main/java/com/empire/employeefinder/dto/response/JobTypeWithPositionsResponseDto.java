package com.empire.employeefinder.dto.response;

import com.empire.employeefinder.model.JobType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobTypeWithPositionsResponseDto {

    private JobType jobType;

    private List<JobPositionResponseDto> positions;
}
