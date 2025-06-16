package com.empire.employeefinder.mapper;

import com.empire.employeefinder.dto.request.EmployeeRequestDto;
import com.empire.employeefinder.dto.response.EmployeeResponseDto;
import com.empire.employeefinder.exception.JobPositionNotFoundException;
import com.empire.employeefinder.exception.JobTypeNotFoundException;
import com.empire.employeefinder.model.Employee;
import com.empire.employeefinder.model.JobPosition;
import com.empire.employeefinder.model.JobType;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = ResumeMapper.class)
public interface EmployeeMapper {

    @Mapping(target = "jobType", source = "jobTypeId", qualifiedByName = "resolveJobTypeById")
    @Mapping(target = "jobPosition", source = "jobPositionId", qualifiedByName = "resolveJobPositionById")
    @Mapping(target = "resume", ignore = true)
    Employee toEntity(EmployeeRequestDto dto, @Context List<JobType> allJobTypes, @Context List<JobPosition> allJobPositions);

    @Mapping(source = "jobType.name", target = "jobType")
    @Mapping(source = "jobPosition.name", target = "jobPosition")
    EmployeeResponseDto toResponse(Employee entity);

    List<EmployeeResponseDto> toDtos(List<Employee> employees);

    @Named("resolveJobTypeById")
    default JobType resolveJobTypeById(Long id, @Context List<JobType> allJobTypes) {
        if (id == null) return null;

        return allJobTypes.stream()
                .filter(type -> type.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new JobTypeNotFoundException("JobType not found for ID: " + id));
    }

    @Named("resolveJobPositionById")
    default JobPosition resolveJobPositionById(Long id, @Context List<JobPosition> allJobPositions) {
        if (id == null) return null;

        return allJobPositions.stream()
                .filter(pos -> pos.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new JobPositionNotFoundException("JobPosition not found for ID: " + id));
    }
}
