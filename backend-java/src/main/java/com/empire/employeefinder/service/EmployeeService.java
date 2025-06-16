package com.empire.employeefinder.service;

import com.empire.employeefinder.dto.request.EmployeeFilterRequestDto;
import com.empire.employeefinder.dto.request.EmployeeRequestDto;
import com.empire.employeefinder.dto.response.EmployeeResponseDto;
import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.Location;
import com.empire.employeefinder.model.enums.Status;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto add(EmployeeRequestDto employeeRequestDto, Principal principal) throws IOException;

    EmployeeResponseDto update(Long employeeId, EmployeeRequestDto employeeRequestDto, Principal principal) throws IOException;

    void changeEmployeeStatus(Long employeeId, Status newStatus, String login);

    EmployeeResponseDto getById(Long employeeId);

    void delete(Long employeeId, Principal principal);

    List<EmployeeResponseDto> filterEmployees(EmployeeFilterRequestDto dto, String sortField, String sortDirection,
                                              int pageSize, int pageNumber, String searchField, String parameter);

    List<EmployeeResponseDto> filterByJobTypeIdAndPositionIdWithSearch(
            Long jobTypeId, Long jobPositionId, Gender gender, Location location,
            String searchField, String parameter,
            String sortField, String sortDirection,
            int pageSize, int pageNumber);

    List<EmployeeResponseDto> getAll(String searchField, String parameter, String status,
                                     String sortField, String sortDirection,
                                     int pageSize, int pageNumber);

    List<EmployeeResponseDto> getAllForUser(String searchField, String parameter,
                                            String sortField, String sortDirection,
                                            int pageSize, int pageNumber);
}
