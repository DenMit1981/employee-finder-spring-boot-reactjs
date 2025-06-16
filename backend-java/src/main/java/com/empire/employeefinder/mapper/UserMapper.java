package com.empire.employeefinder.mapper;

import com.empire.employeefinder.dto.request.UserRegisterRequestDto;
import com.empire.employeefinder.dto.response.UserLoginResponseDto;
import com.empire.employeefinder.dto.response.UserRegisterResponseDto;
import com.empire.employeefinder.dto.response.UserResponseDto;
import com.empire.employeefinder.model.User;
import com.empire.employeefinder.model.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", expression = "java(password)")
    User toUser(UserRegisterRequestDto userRegisterRequestDto, String password, Role role);

    @Mapping(source = "user.company.companyName", target = "companyName")
    @Mapping(source = "user.company.regNumber", target = "regNumber")
    UserRegisterResponseDto toUserRegisterResponseDto(User user, String message);

    @Mapping(source = "user.name", target = "username")
    @Mapping(source = "user.company.companyName", target = "companyName")
    @Mapping(source = "user.company.regNumber", target = "regNumber")
    UserLoginResponseDto toUserLoginResponseDto(User user, String token);

    @Mapping(source = "user.company.companyName", target = "companyName")
    @Mapping(source = "user.company.regNumber", target = "regNumber")
    UserResponseDto toUserResponseDto(User user);

    default List<UserResponseDto> toDtos(List<User> users) {
        return users.stream()
                .map(this::toUserResponseDto)
                .collect(Collectors.toList());
    }
}
