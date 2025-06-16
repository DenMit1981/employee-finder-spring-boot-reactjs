package com.empire.employeefinder.service;

import com.empire.employeefinder.dto.response.JobPositionResponseDto;
import com.empire.employeefinder.dto.response.JobTypeWithPositionsResponseDto;
import com.empire.employeefinder.model.JobType;
import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.Location;

import java.util.List;

public interface JobService {

    List<JobPositionResponseDto> getAllJobPositionsFromNewAndSelectedEmployees();

    List<JobPositionResponseDto> getJobPositionsByJobType(Long jobTypeId);

    List<JobPositionResponseDto> getJobPositionsByJobTypeFromNewAndSelectedEmployees(Long jobTypeId);

    List<JobTypeWithPositionsResponseDto> getAllJobTypesWithPositions();

    List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByGender(Gender gender);

    List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByLocation(Location location);

    List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByGenderAndLocation(Gender gender, Location location);

    List<JobType> getAllJobTypes();

    List<JobType> getJobTypesFromNewAndSelectedEmployees();

    List<JobPositionResponseDto> getAllJobPositions();
}
