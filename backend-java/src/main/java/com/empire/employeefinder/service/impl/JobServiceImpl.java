package com.empire.employeefinder.service.impl;

import com.empire.employeefinder.dto.response.JobPositionResponseDto;
import com.empire.employeefinder.dto.response.JobTypeWithPositionsResponseDto;
import com.empire.employeefinder.exception.JobTypeNotFoundException;
import com.empire.employeefinder.mapper.JobPositionMapper;
import com.empire.employeefinder.model.JobPosition;
import com.empire.employeefinder.model.JobType;
import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.Location;
import com.empire.employeefinder.repository.EmployeeRepository;
import com.empire.employeefinder.repository.JobPositionRepository;
import com.empire.employeefinder.repository.JobTypeRepository;
import com.empire.employeefinder.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private static final String JOB_TYPE_NOT_FOUND = "Job type with id %s not found";

    private final EmployeeRepository employeeRepository;
    private final JobPositionRepository jobPositionRepository;
    private final JobTypeRepository jobTypeRepository;
    private final JobPositionMapper jobPositionMapper;

    @Override
    public List<JobType> getAllJobTypes() {
        return jobTypeRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<JobType> getJobTypesFromNewAndSelectedEmployees() {
        return employeeRepository.findAllJobTypes();
    }

    @Override
    public List<JobPositionResponseDto> getAllJobPositions() {
        return jobPositionMapper.toDtos(jobPositionRepository.findAllByOrderByNameAsc());
    }

    @Override
    public List<JobPositionResponseDto> getAllJobPositionsFromNewAndSelectedEmployees() {
        return jobPositionMapper.toDtos(employeeRepository.findJobPositionsWithStatusNewOrSelected());
    }

    @Override
    public List<JobPositionResponseDto> getJobPositionsByJobType(Long jobTypeId) {
        JobType jobType = findJobTypeById(jobTypeId);

        List<JobPosition> positions = jobPositionRepository.findAllByJobTypeOrderByNameAsc(jobType).stream()
                .sorted(Comparator.comparing(JobPosition::getName, String.CASE_INSENSITIVE_ORDER)).toList();
        return jobPositionMapper.toDtos(positions);
    }

    @Override
    public List<JobPositionResponseDto> getJobPositionsByJobTypeFromNewAndSelectedEmployees(Long jobTypeId) {
        JobType jobType = findJobTypeById(jobTypeId);
        List<JobPosition> positions = employeeRepository.findJobPositionsByJobType(jobType).stream()
                .sorted(Comparator.comparing(JobPosition::getName, String.CASE_INSENSITIVE_ORDER)).toList();
        return jobPositionMapper.toDtos(positions);
    }

    @Override
    public List<JobTypeWithPositionsResponseDto> getAllJobTypesWithPositions() {
        return buildJobTypeWithPositions(employeeRepository.findAllJobTypes(), null, null);
    }

    @Override
    public List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByGender(Gender gender) {
        return buildJobTypeWithPositions(employeeRepository.findJobTypesByGender(gender), gender, null);
    }

    @Override
    public List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByLocation(Location location) {
        return buildJobTypeWithPositions(employeeRepository.findJobTypesByLocation(location), null, location);
    }

    @Override
    public List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByGenderAndLocation(Gender gender, Location location) {
        return buildJobTypeWithPositions(employeeRepository.findJobTypesByGenderAndLocation(gender, location), gender, location);
    }

    private List<JobTypeWithPositionsResponseDto> buildJobTypeWithPositions(List<JobType> jobTypes, Gender gender, Location location) {
        return jobTypes.stream()
                .map(jobType -> Map.entry(jobType, getPositionsSorted(jobType, gender, location)))
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> new JobTypeWithPositionsResponseDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(dto -> dto.getJobType().getName()))
                .toList();
    }

    private List<JobPositionResponseDto> getPositionsSorted(JobType jobType, Gender gender, Location location) {
        List<JobPosition> positions;
        if (gender != null && location != null) {
            positions = employeeRepository.findJobPositionsByJobTypeAndGenderAndLocationAndStatusNewOrSelected(jobType, gender, location);
        } else if (gender != null) {
            positions = employeeRepository.findJobPositionsByJobTypeAndGenderAndStatusNewOrSelected(jobType, gender);
        } else if (location != null) {
            positions = employeeRepository.findJobPositionsByJobTypeAndLocationAndStatusNewOrSelected(jobType, location);
        } else {
            positions = employeeRepository.findJobPositionsByJobTypeAndStatusNewOrSelected(jobType);
        }
        return jobPositionMapper.toDtos(positions);
    }

    private JobType findJobTypeById(Long jobTypeId) {
        return jobTypeRepository.findById(jobTypeId)
                .orElseThrow(() -> new JobTypeNotFoundException(String.format(JOB_TYPE_NOT_FOUND, jobTypeId)));
    }
}
