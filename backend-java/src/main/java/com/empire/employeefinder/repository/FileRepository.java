package com.empire.employeefinder.repository;

import com.empire.employeefinder.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByResume_EmployeeId(Long employeeId);
}

