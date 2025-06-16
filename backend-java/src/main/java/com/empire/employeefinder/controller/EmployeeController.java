package com.empire.employeefinder.controller;

import com.empire.employeefinder.dto.request.EmployeeFilterRequestDto;
import com.empire.employeefinder.dto.request.EmployeeRequestDto;
import com.empire.employeefinder.dto.response.EmployeeResponseDto;
import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.Location;
import com.empire.employeefinder.model.enums.Status;
import com.empire.employeefinder.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Employee controller")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add new employee with resume")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto add(@ModelAttribute @Valid EmployeeRequestDto employeeRequestDto, Principal principal) throws IOException {
        return employeeService.add(employeeRequestDto, principal);
    }

    @PutMapping(value = "/update/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update employee and resume")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponseDto update(@PathVariable Long employeeId, @ModelAttribute @Valid EmployeeRequestDto employeeRequestDto, Principal principal) throws IOException {
        return employeeService.update(employeeId, employeeRequestDto, principal);
    }

    @PutMapping("change-status/{employeeId}")
    @Operation(summary = "Change employee status")
    @ResponseStatus(HttpStatus.OK)
    public void changeStatus(@PathVariable Long employeeId,
                             @RequestParam(value = "status") Status newStatus, Principal principal) {
        employeeService.changeEmployeeStatus(employeeId, newStatus, principal.getName());
    }

    @DeleteMapping("remove/{employeeId}")
    @Operation(summary = "Delete employee by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long employeeId, Principal principal) {
        employeeService.delete(employeeId, principal);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Get employee by ID")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponseDto getById(@PathVariable("employeeId") Long employeeId) {
        return employeeService.getById(employeeId);
    }

    @GetMapping
    @Operation(summary = "Get all employees")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeResponseDto> getAll(
            @RequestParam(value = "searchField", defaultValue = "default") String searchField,
            @RequestParam(value = "parameter", defaultValue = "") String parameter,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "25") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber) {

        return employeeService.getAll(searchField, parameter, status, sortField, sortDirection, pageSize, pageNumber);
    }

    @GetMapping("/for-user")
    @Operation(summary = "Get employees with statuses NEW and SELECTED for user")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeResponseDto> getAllForUser(
            @RequestParam(value = "searchField", defaultValue = "default") String searchField,
            @RequestParam(value = "parameter", defaultValue = "") String parameter,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "25") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber) {

        return employeeService.getAllForUser(searchField, parameter, sortField, sortDirection, pageSize, pageNumber);
    }

    @PostMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeResponseDto> filterEmployees(
            @RequestBody EmployeeFilterRequestDto filterRequestDto,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "25") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String parameter
    ) {
        return employeeService.filterEmployees(
                filterRequestDto,
                sortField,
                sortDirection,
                pageSize,
                pageNumber,
                searchField,
                parameter
        );
    }

    @GetMapping("/filter/job-type-position")
    @Operation(summary = "Filter employees by jobType ID, jobPosition ID, gender, location and optional search")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeResponseDto> filterByJobTypeAndPosition(
            @RequestParam(required = false) Long jobTypeId,
            @RequestParam(required = false) Long jobPositionId,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Location location,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "25") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String parameter
    ) {
        return employeeService.filterByJobTypeIdAndPositionIdWithSearch(
                jobTypeId, jobPositionId, gender, location, searchField,
                parameter, sortField, sortDirection, pageSize, pageNumber);
    }
}
