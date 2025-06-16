package com.empire.employeefinder.service;

import com.empire.employeefinder.dto.request.UserLoginRequestDto;
import com.empire.employeefinder.dto.request.UserRegisterRequestDto;
import com.empire.employeefinder.dto.request.UserUpdateRequestDto;
import com.empire.employeefinder.dto.response.UserLoginResponseDto;
import com.empire.employeefinder.dto.response.UserRegisterResponseDto;
import com.empire.employeefinder.dto.response.UserResponseDto;
import com.empire.employeefinder.model.enums.Role;

import java.util.List;

public interface UserService {

    UserRegisterResponseDto registration(UserRegisterRequestDto userRegisterRequestDto);

    UserLoginResponseDto authentication(UserLoginRequestDto userDto);

    UserResponseDto getById(Long userid);

    List<UserResponseDto> getAll(String searchField, String parameter, String sortField, String sortDirection, int pageSize, int pageNumber, String login);

    UserResponseDto changeUserRole(Long userId, Role newRole);

    void deleteById(Long userId);

    UserRegisterResponseDto changePassword(UserUpdateRequestDto userDto);
}
