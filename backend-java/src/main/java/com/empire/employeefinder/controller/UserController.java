package com.empire.employeefinder.controller;

import com.empire.employeefinder.dto.response.UserResponseDto;
import com.empire.employeefinder.model.enums.Role;
import com.empire.employeefinder.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "User controller")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users with optional search, sort, and pagination")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getAll(
            @RequestParam(value = "searchField", defaultValue = "default") String searchField,
            @RequestParam(value = "parameter", defaultValue = "") String parameter,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "25") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            Principal principal) {
        return userService.getAll(searchField, parameter, sortField, sortDirection, pageSize, pageNumber, principal.getName());
    }

    @PutMapping("/{userId}/change-role")
    @Operation(summary = "Update user role")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto changeRole(@PathVariable Long userId, @RequestParam Role newRole) {
        return userService.changeUserRole(userId, newRole);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}
