package com.empire.employeefinder.repository;

import com.empire.employeefinder.model.Employee;
import com.empire.employeefinder.model.JobPosition;
import com.empire.employeefinder.model.JobType;
import com.empire.employeefinder.model.enums.Gender;
import com.empire.employeefinder.model.enums.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    @Query("SELECT DISTINCT e.jobType FROM Employee e WHERE e.gender = :gender AND e.location = :location")
    List<JobType> findJobTypesByGenderAndLocation(@Param("gender") Gender gender, @Param("location") Location location);

    @Query("SELECT DISTINCT e.jobPosition FROM Employee e " +
            "WHERE e.jobType = :jobType AND e.gender = :gender AND e.location = :location " +
            "AND e.status IN ('NEW', 'SELECTED') ORDER BY e.jobPosition.name")
    List<JobPosition> findJobPositionsByJobTypeAndGenderAndLocationAndStatusNewOrSelected(
            @Param("jobType") JobType jobType,
            @Param("gender") Gender gender,
            @Param("location") Location location);

    @Query("SELECT DISTINCT e.jobPosition FROM Employee e " +
            "WHERE e.jobType = :jobType AND e.gender = :gender AND e.status IN ('NEW', 'SELECTED') ORDER BY e.jobPosition.name")
    List<JobPosition> findJobPositionsByJobTypeAndGenderAndStatusNewOrSelected(
            @Param("jobType") JobType jobType,
            @Param("gender") Gender gender);

    @Query("SELECT DISTINCT e.jobPosition FROM Employee e " +
            "WHERE e.jobType = :jobType AND e.location = :location AND e.status IN ('NEW', 'SELECTED') ORDER BY e.jobPosition.name")
    List<JobPosition> findJobPositionsByJobTypeAndLocationAndStatusNewOrSelected(
            @Param("jobType") JobType jobType,
            @Param("location") Location location);

    @Query("SELECT DISTINCT e.jobPosition FROM Employee e WHERE e.status IN ('NEW', 'SELECTED') ORDER BY e.jobPosition.name")
    List<JobPosition> findJobPositionsWithStatusNewOrSelected();

    @Query("SELECT DISTINCT e.jobPosition FROM Employee e " +
            "WHERE e.jobType = :jobType AND e.status IN ('NEW', 'SELECTED') ORDER BY e.jobPosition.name")
    List<JobPosition> findJobPositionsByJobTypeAndStatusNewOrSelected(@Param("jobType") JobType jobType);

    @Query("SELECT DISTINCT e.jobType FROM Employee e WHERE e.status IN ('NEW', 'SELECTED') ORDER BY e.jobType.name")
    List<JobType> findAllJobTypes();

    @Query("SELECT DISTINCT e.jobType FROM Employee e WHERE e.location = :location ORDER BY e.jobType.name")
    List<JobType> findJobTypesByLocation(@Param("location") Location location);

    @Query("SELECT DISTINCT e.jobType FROM Employee e WHERE e.gender = :gender ORDER BY e.jobType.name")
    List<JobType> findJobTypesByGender(@Param("gender") Gender gender);

    @Query("SELECT DISTINCT e.jobPosition FROM Employee e " +
            "WHERE e.jobType = :jobType AND e.status IN ('NEW', 'SELECTED') " +
            "ORDER BY e.jobPosition.name")
    List<JobPosition> findJobPositionsByJobType(@Param("jobType") JobType jobType);
}
