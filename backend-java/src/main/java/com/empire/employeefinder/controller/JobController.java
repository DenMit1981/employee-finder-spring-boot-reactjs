package com.empire.employeefinder.controller;

import com.empire.employeefinder.dto.response.JobPositionResponseDto;
import com.empire.employeefinder.dto.response.JobTypeWithPositionsResponseDto;
import com.empire.employeefinder.model.JobType;
import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.Location;
import com.empire.employeefinder.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Job controller")
public class JobController {

    private final JobService jobService;

    @GetMapping("/job-types")
    @Operation(summary = "Get all job types")
    @ResponseStatus(HttpStatus.OK)
    public List<JobType> getAllJobTypes() {
        return jobService.getAllJobTypes();
    }

    @GetMapping("/job-types/from-employees")
    @Operation(summary = "Get all job types from employees with status NEW and SELECTED")
    @ResponseStatus(HttpStatus.OK)
    public List<JobType> getJobTypesFromNewEmployees() {
        return jobService.getJobTypesFromNewAndSelectedEmployees();
    }

    @GetMapping("/job-positions")
    @Operation(summary = "Get all job positions")
    @ResponseStatus(HttpStatus.OK)
    public List<JobPositionResponseDto> getAllJobPositions() {
        return jobService.getAllJobPositions();
    }

    @GetMapping("/job-positions/from-employees")
    @Operation(summary = "Get all job positions from employees with status NEW and SELECTED")
    @ResponseStatus(HttpStatus.OK)
    public List<JobPositionResponseDto> getAllJobPositionsFromNewEmployees() {
        return jobService.getAllJobPositionsFromNewAndSelectedEmployees();
    }

    @GetMapping("/job-types-positions")
    @Operation(summary = "Get all job positions by job type")
    @ResponseStatus(HttpStatus.OK)
    public List<JobPositionResponseDto> getJobPositionsByJobType(@RequestParam Long jobTypeId) {
        return jobService.getJobPositionsByJobType(jobTypeId);
    }

    @GetMapping("/job-types-positions/from-employees")
    @Operation(summary = "Get all job positions by job type from employees with status NEW and SELECTED")
    @ResponseStatus(HttpStatus.OK)
    public List<JobPositionResponseDto> getJobPositionsByJobTypeFromNewAndSelectedEmployees(@RequestParam Long jobTypeId) {
        return jobService.getJobPositionsByJobTypeFromNewAndSelectedEmployees(jobTypeId);
    }

    @GetMapping("/job-types-positions/all")
    @Operation(summary = "Get all job types with positions")
    @ResponseStatus(HttpStatus.OK)
    public List<JobTypeWithPositionsResponseDto> getAllJobTypesWithPositions() {
        return jobService.getAllJobTypesWithPositions();
    }

    @GetMapping("/job-types-positions/gender")
    @Operation(summary = "Get job types with positions by gender")
    @ResponseStatus(HttpStatus.OK)
    public List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByGender(@RequestParam Gender gender) {
        return jobService.getJobTypesWithPositionsByGender(gender);
    }

    @GetMapping("/job-types-positions/location")
    @Operation(summary = "Get job types with positions by location")
    @ResponseStatus(HttpStatus.OK)
    public List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByLocation(@RequestParam Location location) {
        return jobService.getJobTypesWithPositionsByLocation(location);
    }

    @GetMapping("/job-types-positions/gender-location")
    @Operation(summary = "Get job types with positions by gender and location")
    @ResponseStatus(HttpStatus.OK)
    public List<JobTypeWithPositionsResponseDto> getJobTypesWithPositionsByGenderAndLocation(
            @RequestParam Gender gender,
            @RequestParam Location location) {
        return jobService.getJobTypesWithPositionsByGenderAndLocation(gender, location);
    }
}
