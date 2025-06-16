package com.empire.employeefinder.service;

import com.empire.employeefinder.dto.response.SelectionResponseDto;

public interface SelectionService {

    SelectionResponseDto submitFinalSelection(String login);

    SelectionResponseDto addEmployeeToSelection(String login, Long employeeId);

    SelectionResponseDto removeEmployeeFromSelection(String login, Long employeeId);

    void clearSelection(String login);

    SelectionResponseDto getCurrentSelection(String login);

    SelectionResponseDto getById(Long selectionId, String searchField,
                                 String parameter, String sortField, String sortDirection,
                                 int pageSize, int pageNumber);
}
