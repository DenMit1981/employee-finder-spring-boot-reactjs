package com.empire.employeefinder.repository;

import com.empire.employeefinder.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByEmployeeId(Long employeeId);
}
