package com.empire.employeefinder.mapper;

import com.empire.employeefinder.dto.request.CompanyRequestDto;
import com.empire.employeefinder.model.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company toCompany(CompanyRequestDto companyRequestDto);
}
