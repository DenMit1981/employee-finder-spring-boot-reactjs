package com.empire.employeefinder.controller;

import com.empire.employeefinder.dto.response.SelectionResponseDto;
import com.empire.employeefinder.service.SelectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/selections")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Selection controller")
public class SelectionController {

    private final SelectionService selectionService;

    @PostMapping("/add-employee/{employeeId}")
    @Operation(summary = "Add employee to selection")
    @ResponseStatus(HttpStatus.CREATED)
    public SelectionResponseDto addEmployeeToSelection(Principal principal, @PathVariable("employeeId") Long employeeId) {
        return selectionService.addEmployeeToSelection(principal.getName(), employeeId);
    }

    @DeleteMapping("/remove-employee/{employeeId}")
    @Operation(summary = "Remove employee from selection")
    @ResponseStatus(HttpStatus.OK)
    public SelectionResponseDto removeEmployeeFromSelection(Principal principal, @PathVariable Long employeeId) {
        return selectionService.removeEmployeeFromSelection(principal.getName(), employeeId);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear all selected candidates from selection (e.g. on logout)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearSelection(Principal principal) {
        selectionService.clearSelection(principal.getName());
    }

    @GetMapping("/current")
    @Operation(summary = "Get current unsubmitted selection")
    public SelectionResponseDto getCurrentSelection(Principal principal) {
        return selectionService.getCurrentSelection(principal.getName());
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit final selection and send to admins emails")
    @ResponseStatus(HttpStatus.CREATED)
    public SelectionResponseDto submitFinalSelection(Principal principal) {
        return selectionService.submitFinalSelection(principal.getName());
    }

    @GetMapping("/{selectionId}")
    @Operation(summary = "Get selection by ID")
    @ResponseStatus(HttpStatus.OK)
    public SelectionResponseDto getById(@PathVariable("selectionId") Long selectionId,
                                        @RequestParam(value = "searchField", defaultValue = "default") String searchField,
                                        @RequestParam(value = "parameter", defaultValue = "") String parameter,
                                        @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                        @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                        @RequestParam(value = "pageSize", defaultValue = "25") int pageSize,
                                        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber) {
        return selectionService.getById(selectionId, searchField, parameter, sortField, sortDirection, pageSize, pageNumber);
    }
}
