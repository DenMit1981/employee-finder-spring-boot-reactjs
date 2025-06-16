package com.empire.employeefinder.mapper;

import com.empire.employeefinder.dto.response.EmployeeResponseDto;
import com.empire.employeefinder.dto.response.SelectionResponseDto;
import com.empire.employeefinder.model.Selection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = EmployeeMapper.class)
public interface SelectionMapper {

    @Mapping(target = "companyName", expression = "java(selection.getCompany().getCompanyName())")
    @Mapping(target = "regNumber", expression = "java(selection.getCompany().getRegNumber())")
    @Mapping(source = "chosenCandidates", target = "candidates")
    SelectionResponseDto toResponse(Selection selection);

    @Mapping(target = "companyName", expression = "java(selection.getCompany().getCompanyName())")
    @Mapping(target = "regNumber", expression = "java(selection.getCompany().getRegNumber())")
    SelectionResponseDto toResponse(Selection selection, List<EmployeeResponseDto> candidates);
}
