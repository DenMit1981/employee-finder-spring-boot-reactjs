package com.empire.employeefinder.mapper;

import com.empire.employeefinder.dto.response.JobPositionResponseDto;
import com.empire.employeefinder.model.JobPosition;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobPositionMapper {

    JobPositionResponseDto toResponse(JobPosition jobPosition);

    List<JobPositionResponseDto> toDtos(List<JobPosition> jobPositions);
}
